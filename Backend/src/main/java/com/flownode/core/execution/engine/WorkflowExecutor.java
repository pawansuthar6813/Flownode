package com.flownode.core.execution.engine;

import com.flownode.core.execution.context.WorkflowExecutionContext;
import com.flownode.core.execution.node.Node;
import com.flownode.core.execution.node.impl.condition.ConditionNode;
import com.flownode.core.execution.readiness.DefaultJoinReadinessEvaluator;
import com.flownode.core.execution.readiness.JoinReadinessEvaluator;
import com.flownode.core.state.NodeState;
import com.flownode.core.state.WorkflowState;
import com.flownode.core.workflow.Workflow;

import java.util.HashSet;
import java.util.Set;

public class WorkflowExecutor {

    private final Set<String> executedNodes = new HashSet<>();
    private final Set<String> skippedNodes = new HashSet<>();

    // STEP 6 → Join readiness evaluator
    private final JoinReadinessEvaluator readinessEvaluator = new DefaultJoinReadinessEvaluator();

    public void execute(Workflow workflow) throws Exception {

        if (workflow == null) {
            throw new IllegalArgumentException("Workflow cannot be null");
        }

        workflow.setWorkflowState(WorkflowState.RUNNING);

        WorkflowExecutionContext context = new WorkflowExecutionContext();

        if (workflow.getStartNodeId() == null) {
            throw new RuntimeException("Start node not defined");
        }

        traverseAndExecute(workflow, workflow.getStartNodeId(), context);

        workflow.setWorkflowState(WorkflowState.COMPLETED);
    }

    // ----------------------------------------------------

    private void traverseAndExecute(Workflow workflow,
                                    String currentNodeId,
                                    WorkflowExecutionContext context) throws Exception {

        // Prevent duplicate execution
        if (executedNodes.contains(currentNodeId) || skippedNodes.contains(currentNodeId)) {
            return;
        }

        // STEP 6 → Join readiness check
        if (!readinessEvaluator.isNodeReady(currentNodeId, workflow, context)) {
            return; // Wait until parents resolve
        }

        // Fetch node
        Node currentNode = workflow
                .getNode(currentNodeId)
                .orElseThrow(() -> new RuntimeException("Node not found: " + currentNodeId));

        // Mark READY
        context.setNodeState(currentNodeId, NodeState.READY);

        // Mark RUNNING
        context.setNodeState(currentNodeId, NodeState.RUNNING);

        // Execute
        currentNode.execute(context);

        // Mark COMPLETED
        context.setNodeState(currentNodeId, NodeState.COMPLETED);

        executedNodes.add(currentNodeId);


        // ---------------------CONDITION ROUTING--------------------

        if (currentNode instanceof ConditionNode) {

            String decision = (String) context.get(currentNodeId + "_condition");

            String trueNode = workflow.getTrueNodeId(currentNodeId);

            String falseNode = workflow.getFalseNodeId(currentNodeId);

            if ("TRUE".equals(decision)) {

                if (trueNode != null) {
                    traverseAndExecute(workflow, trueNode, context);
                }

                if (falseNode != null) {
                    propagateSkip(workflow, falseNode, context);
                }

            } else {

                if (falseNode != null) {
                    traverseAndExecute(workflow, falseNode, context);
                }

                if (trueNode != null) {
                    propagateSkip(workflow, trueNode, context);
                }
            }

            return;
        }


        // -----------------------NORMAL TRAVERSAL-------------------------

        for (String nextNodeId : workflow.getNextNodesId(currentNodeId)) {

            traverseAndExecute(workflow, nextNodeId, context);
        }
    }

    // ----------------------------------------------------

    private void propagateSkip(Workflow workflow, String nodeId, WorkflowExecutionContext context) {

        if (skippedNodes.contains(nodeId)) {
            return;
        }

        Node node = workflow.getNode(nodeId).orElse(null);

        if (node == null) {
            return;
        }

        // Mark SKIPPED
        context.setNodeState(nodeId, NodeState.SKIPPED);
        skippedNodes.add(nodeId);

        System.out.println("Node Skipped → " + nodeId);

        // ---------- FIX: Join-aware propagation ----------

        for (String next : workflow.getNextNodesId(nodeId)) {

            int parentCount = workflow.getParents(next).size();

            if (parentCount > 1) {

                // This is a join node → do NOT auto-skip
                System.out.println("Join detected — skip propagation halted at → " + next);

                // NEW: Re-evaluate readiness
                try {
                    traverseAndExecute(workflow, next, context);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                continue;
            }

            // Safe to propagate skip
            propagateSkip(workflow, next, context);
        }
    }

}
