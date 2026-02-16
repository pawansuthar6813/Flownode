package com.flownode.core.execution.node.impl.condition;

import com.flownode.core.execution.context.WorkflowExecutionContext;

public class BooleanConditionNode extends ConditionNode {

    private String contextKey;

    public BooleanConditionNode(String nodeId, String nodeName, String contextKey) {
        super(nodeId, nodeName);
        this.contextKey = contextKey;
    }

    @Override
    protected boolean evaluate(WorkflowExecutionContext context) {

        Object value = context.get(contextKey);

        return value instanceof Boolean && (Boolean) value;
    }
}
