package com.flownode.core.execution.node;

import com.flownode.core.execution.context.WorkflowExecutionContext;
import com.flownode.core.execution.resolution.policy.ResolutionPolicy;
import com.flownode.core.state.NodeState;

public interface Node {

    String getNodeId();

    String getNodeName();

    @Deprecated
    NodeState getNodeState();

    @Deprecated
    public void setNodeState(NodeState state);

    void execute(WorkflowExecutionContext context);

    ResolutionPolicy getResolutionPolicy();
}
