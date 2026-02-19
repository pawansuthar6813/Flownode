package com.flownode.core.execution.engine.task;

import com.flownode.core.execution.context.WorkflowExecutionContext;
import com.flownode.core.execution.engine.executor.ConcurrentWorkflowExecutor;
import com.flownode.core.execution.node.Node;
import com.flownode.core.workflow.Workflow;

public class NodeExecutionTask implements Runnable{

    private final String nodeId;
    private final Workflow workflow;
    private final WorkflowExecutionContext context;
    private final ConcurrentWorkflowExecutor executor;

    public NodeExecutionTask(String nodeId, Workflow workflow, WorkflowExecutionContext context, ConcurrentWorkflowExecutor executor) {
        this.nodeId = nodeId;
        this.workflow = workflow;
        this.context = context;
        this.executor = executor;
    }

    @Override
    public void run() {

        boolean success = false;
        Exception caughtException = null;

        try {
            Node node = workflow.getNode(nodeId)
                    .orElseThrow(() -> new RuntimeException("Node not found with id: " + nodeId));

            node.execute(context);

            success = true;

        } catch (Exception e) {
            caughtException = e;

        } finally {

            // ✅ Handle success/failure FIRST
            if (success) {
                executor.addExecutedNodes(nodeId);
                executor.onNodeExecutionSuccess(nodeId, workflow, context);
            } else {
                executor.onNodeExecutionFailed(nodeId, workflow, context, caughtException);
            }

            // ✅ Decrement AFTER children are submitted
            executor.decrementActiveTasks();

            // ✅ Check completion AFTER decrement
            executor.checkForWorkflowCompletion(workflow);
        }
    }
}
