package com.flownode.core.execution.node.impl.task;

import com.flownode.core.execution.context.WorkflowExecutionContext;

import java.util.function.Consumer;

public class ManualApprovalTaskNode extends TaskNode{

    public ManualApprovalTaskNode(String nodeId, String nodeName) {
        super(nodeId, nodeName);
    }

    @Override
    protected void process(WorkflowExecutionContext context) {

        System.out.println("Waiting for manual approval...");

        // Simulated approval
        context.put("approved", true);
    }
}
