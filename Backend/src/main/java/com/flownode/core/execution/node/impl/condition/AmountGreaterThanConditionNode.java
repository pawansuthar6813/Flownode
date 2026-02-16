package com.flownode.core.execution.node.impl.condition;

import com.flownode.core.execution.context.WorkflowExecutionContext;

public class AmountGreaterThanConditionNode extends ConditionNode {

    private final String contextKey;
    private final double threshold;

    public AmountGreaterThanConditionNode(String nodeId, String nodeName, String contextKey, double threshold) {
        super(nodeId, nodeName);
        this.contextKey = contextKey;
        this.threshold = threshold;
    }

//    here contextKey is not a value, it is a field name and the value we need to compare should come from previous node execution

    @Override
    protected boolean evaluate(WorkflowExecutionContext context) {

        Object value = context.get(contextKey);

        if (value == null) {
            throw new RuntimeException("Missing context value for key: " + contextKey);
        }

        double amount = Double.parseDouble(value.toString());

        return amount > threshold;
    }
}

