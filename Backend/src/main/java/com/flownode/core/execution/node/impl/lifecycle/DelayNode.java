package com.flownode.core.execution.node.impl.lifecycle;

import com.flownode.core.execution.context.WorkflowExecutionContext;
import com.flownode.core.execution.node.BaseNode;

public class DelayNode extends BaseNode {

    private long delayMillis;

    public DelayNode(String nodeId, String nodeName, long delayMillis) {
        super(nodeId, nodeName);
        this.delayMillis = delayMillis;
    }

    @Override
    protected void process(WorkflowExecutionContext context) {

        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
