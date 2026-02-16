package com.flownode.core.execution.node.impl.condition;

import com.flownode.core.execution.context.WorkflowExecutionContext;
import com.flownode.core.execution.node.BaseNode;

public abstract class ConditionNode extends BaseNode {

    public ConditionNode(String nodeId, String nodeName){
        super(nodeId, nodeName);
    }

    @Override
    protected void process(WorkflowExecutionContext context) {
        boolean result = evaluate(context);

        if (result) {
            System.out.println(nodeName + " → Condition TRUE");
            context.put(nodeId + "_condition", "TRUE");
        } else {
            System.out.println(nodeName + " → Condition FALSE");
            context.put(nodeId + "_condition", "FALSE");
        }
    }

    // Subclasses define decision logic
    protected abstract boolean evaluate(WorkflowExecutionContext context);
}
