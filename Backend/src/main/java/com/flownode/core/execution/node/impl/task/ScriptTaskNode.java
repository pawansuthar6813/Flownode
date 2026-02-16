package com.flownode.core.execution.node.impl.task;

import com.flownode.core.execution.context.WorkflowExecutionContext;

import java.util.function.Consumer;

public class ScriptTaskNode extends TaskNode {

    private Consumer<WorkflowExecutionContext> script;

    public ScriptTaskNode(String nodeId, String nodeName, Consumer<WorkflowExecutionContext> script) {
        super(nodeId, nodeName);
        this.script = script;
    }

    @Override
    protected void process(WorkflowExecutionContext context) {
        script.accept(context);
    }
}
