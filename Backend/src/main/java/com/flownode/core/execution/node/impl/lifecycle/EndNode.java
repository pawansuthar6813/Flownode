package com.flownode.core.execution.node.impl.lifecycle;

import com.flownode.core.execution.context.WorkflowExecutionContext;
import com.flownode.core.execution.node.BaseNode;

public class EndNode extends BaseNode {


    public EndNode(String nodeId, String nodeName) {
        super(nodeId, nodeName);
    }

    @Override
    protected void process(WorkflowExecutionContext context) {
        System.out.println("Workflow ended");
    }
}
