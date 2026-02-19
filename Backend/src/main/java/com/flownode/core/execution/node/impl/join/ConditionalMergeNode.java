package com.flownode.core.execution.node.impl.join;

import com.flownode.core.execution.context.WorkflowExecutionContext;
import com.flownode.core.execution.node.BaseNode;
import com.flownode.core.execution.resolution.policy.AllResolvedPolicy;

public class ConditionalMergeNode extends BaseNode {

    public ConditionalMergeNode(String nodeId, String nodeName) {
        super(nodeId, nodeName);
        this.resolutionPolicy = new AllResolvedPolicy();
    }

    @Override
    protected void process(WorkflowExecutionContext context) {
        System.out.println(nodeName + " → Conditional merge completed");
    }
}
