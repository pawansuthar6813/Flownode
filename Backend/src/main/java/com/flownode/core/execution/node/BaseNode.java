package com.flownode.core.execution.node;


import com.flownode.core.execution.context.WorkflowExecutionContext;
import com.flownode.core.state.NodeState;

public abstract class BaseNode implements Node{

    protected String nodeId;
    protected String nodeName;
    protected NodeState nodeState;


    public BaseNode(String nodeId, String nodeName) {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.nodeState = NodeState.CREATED;
    }

    @Override
    public final void execute(WorkflowExecutionContext context){
        try {
            markRunning();

            beforeExecute(context);

            process(context);   // Core business logic (abstract)

            afterExecute(context);

            markCompleted();

        } catch (Exception e) {

            markFailed();

            onFailure(context, e);

            // Re-throw so executor can handle workflow-level failure
            throw new RuntimeException(
                    "Node execution failed: " + nodeId, e
            );
        }
    }

    @Override
    public String getNodeId() {
        return this.nodeId;
    }

    @Override
    public NodeState getNodeState() {
        return this.nodeState;
    }

    @Override
    public String getNodeName(){
        return this.nodeName;
    }

    @Override
    public void setNodeState(NodeState state) {
        this.nodeState = state;
    }


    protected abstract void process(WorkflowExecutionContext context);

    protected void beforeExecute(WorkflowExecutionContext context) {
        // Default: No-operation
    }

    protected void afterExecute(WorkflowExecutionContext context) {
        // Default: No-operation
    }

    protected void onFailure(WorkflowExecutionContext context, Exception e){
        // Default: No-operation
    }

    protected void markRunning(){
        this.nodeState = NodeState.RUNNING;
    }

    protected void markCompleted() {
        this.nodeState = NodeState.COMPLETED;
    }

    protected void markFailed() {
        this.nodeState = NodeState.FAILED;
    }
}
