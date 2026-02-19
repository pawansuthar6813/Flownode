package com.flownode.core.execution.engine.executor;

import com.flownode.core.execution.context.WorkflowExecutionContext;
import com.flownode.core.execution.engine.task.NodeExecutionTask;
import com.flownode.core.execution.node.Node;
import com.flownode.core.execution.node.impl.condition.ConditionNode;
import com.flownode.core.execution.readiness.DefaultJoinReadinessEvaluator;
import com.flownode.core.execution.resolution.ResolutionSnapshot;
import com.flownode.core.state.NodeState;
import com.flownode.core.workflow.Workflow;


import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentWorkflowExecutor {

    // Thread pool for parallel node execution
    private final ExecutorService executorService;

    // Tracks currently running node tasks
    private final AtomicInteger activeTaskCount = new AtomicInteger(0);

    // Tracks nodes already scheduled for execution
    private final Set<String> scheduledNodes = ConcurrentHashMap.newKeySet();

    private final CountDownLatch completionLatch = new CountDownLatch(1);

    private final Set<String> executedNodes = ConcurrentHashMap.newKeySet();
    private final Set<String> skippedNodes = ConcurrentHashMap.newKeySet();
    private final WorkflowExecutionContext context;

    private Map<String, ResolutionSnapshot> snapshotStore
            = new ConcurrentHashMap<>();


    public ConcurrentWorkflowExecutor(int threadPoolSize) {
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        this.context = new WorkflowExecutionContext();
    }

    // Prevent duplicate scheduling
    public boolean markNodeScheduled(String nodeId) {
        return scheduledNodes.add(nodeId); // called when node is ready to execute, submitNodeForExecution is responsible to call this method
    }

    // Active task tracking
    public void incrementActiveTasks() {
        activeTaskCount.incrementAndGet(); // Called when node execution starts.
    }

    public void decrementActiveTasks() {
        activeTaskCount.decrementAndGet();  // Called when node execution finishes.
    }

    public int getActiveTaskCount() {
        return activeTaskCount.get();
    }

    // Executor access
    public ExecutorService getExecutorService() {
        return executorService;
    }

    // Graceful shutdown
    public void shutdown() {
        executorService.shutdown();
    }

    public void startWorkflow(Workflow workflow, Map<String, Object> inputs) {

        // 2️⃣ Load inputs
        if (inputs != null) {
            inputs.forEach(context::put);
        }

        // 3️⃣ Get start node
        String startNodeId = workflow.getStartNodeId();

        if (startNodeId == null) {
            throw new RuntimeException("Start node not configured");
        }

        // 4️⃣ Submit start node
        submitNodeForExecution(startNodeId, workflow, context);
    }


    public void addExecutedNodes(String nodeId){
        executedNodes.add(nodeId);
    }

    public int getExecutedNodes(){
        return executedNodes.size();
    }

    public void setSkippedNodes(String nodeId){
        skippedNodes.add(nodeId);
    }

    public Set<String> getSkippedNodes(){
        return Collections.unmodifiableSet(skippedNodes);
    }


    public void submitNodeForExecution(String nodeId, Workflow workflow, WorkflowExecutionContext context) {

        // Prevent duplicate scheduling
        boolean scheduled = markNodeScheduled(nodeId);

        if (!scheduled) {
            return; // Already scheduled → skip
        }

        // Increment active task count
        incrementActiveTasks();

        // Create execution task
        NodeExecutionTask task = new NodeExecutionTask(nodeId, workflow, context, this);

        // Submit to thread pool
        executorService.submit(task);
    }

    public void onNodeExecutionSuccess(String nodeId, Workflow workflow, WorkflowExecutionContext context) {

        context.setNodeState(nodeId, NodeState.COMPLETED);

        Node currNode = workflow.getNode(nodeId)
                .orElseThrow(() -> new RuntimeException("Node not found: " + nodeId));

        if (currNode instanceof ConditionNode) {

            String decision = (String) context.get(nodeId + "_condition");
            String trueNodeId = workflow.getTrueNodeId(nodeId);
            String falseNodeId = workflow.getFalseNodeId(nodeId);

            if ("TRUE".equals(decision)) {
                submitIfReady(trueNodeId, workflow, context);
                propagateSkip(falseNodeId, workflow, context);
            } else {
                submitIfReady(falseNodeId, workflow, context);
                propagateSkip(trueNodeId, workflow, context);
            }

        } else {
            // Centralized — every child goes through submitIfReady()
            for (String childId : workflow.getNextNodesId(nodeId)) {
                submitIfReady(childId, workflow, context);
            }
        }
    }

    public void onNodeExecutionFailed(String nodeId, Workflow workflow, WorkflowExecutionContext context, Exception e) {

        context.setNodeState(nodeId, NodeState.FAILED);

        // Log failure
        System.err.println("Node execution failed: " + nodeId + " | Error: " + e.getMessage());

        // Decide traversal policy
        handleFailureTraversal(nodeId, workflow, context);

        checkForWorkflowCompletion(workflow);

    }


    public void triggerChildNodes(String nodeId, Workflow workflow, WorkflowExecutionContext context){

        // get children from workflow
        List<String> childrenId = workflow.getNextNodesId(nodeId);

        for (String id : childrenId){
            submitIfReady(id, workflow, context);
        }
    }

    public void checkForWorkflowCompletion(Workflow workflow) {

        int totalNodes = workflow.getAllNodes().size();

        int resolved =
                executedNodes.size() +
                        skippedNodes.size();

        if (resolved == totalNodes) {

            System.out.println("Workflow execution completed successfully.");

            completionLatch.countDown();
            shutdown();
        }
    }


    public void awaitCompletion() throws InterruptedException {
        completionLatch.await();  // block until workflow done
    }

    private void handleFailureTraversal(String nodeId, Workflow workflow, WorkflowExecutionContext context){
        System.err.println("Workflow marked failed due to node: " + nodeId);

        shutdown();
    }

    private void propagateSkip(String nodeId, Workflow workflow, WorkflowExecutionContext context) {

        if (skippedNodes.contains(nodeId)) return;

        Node node = workflow.getNode(nodeId).orElse(null);
        if (node == null) return;

        // Check if ALL parents are resolved before skipping
        List<String> parents = workflow.getParents(nodeId);
        boolean allParentsResolved = parents.stream()
                .allMatch(p -> skippedNodes.contains(p)
                        || context.getNodeState(p) == NodeState.COMPLETED);

        if (!allParentsResolved) return;  // wait until all parents resolved

        context.setNodeState(nodeId, NodeState.SKIPPED);
        skippedNodes.add(nodeId);
        System.out.println("Node Skipped → " + nodeId);

        for (String nextId : getAllChildren(nodeId, workflow)) {
            List<String> nextParents = workflow.getParents(nextId);

            boolean allSkipped = nextParents.stream()
                    .allMatch(p -> skippedNodes.contains(p));

            if (allSkipped) {
                propagateSkip(nextId, workflow, context);
            } else {
                submitIfReady(nextId, workflow, context);
            }
        }
    }

    public void submitIfReady(String nodeId, Workflow workflow, WorkflowExecutionContext context){

        DefaultJoinReadinessEvaluator evaluator =
                new DefaultJoinReadinessEvaluator();

        boolean isReady = evaluator.isNodeReady(nodeId, workflow, context);
        System.out.println("submitIfReady → " + nodeId + " | ready=" + isReady);
        if(isReady) submitNodeForExecution(nodeId, workflow, context);
    }

    private List<String> getAllChildren(String nodeId, Workflow workflow) {

        List<String> children = new ArrayList<>();

        children.addAll(workflow.getNextNodesId(nodeId));

        String t = workflow.getTrueNodeId(nodeId);
        String f = workflow.getFalseNodeId(nodeId);

        if (t != null) children.add(t);
        if (f != null) children.add(f);

        return children;
    }


}
