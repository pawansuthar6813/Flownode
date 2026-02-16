package com.flownode.core.execution.readiness;

import com.flownode.core.execution.context.WorkflowExecutionContext;
import com.flownode.core.workflow.Workflow;


public interface JoinReadinessEvaluator {

    boolean isNodeReady(
            String nodeId,
            Workflow workflow,
            WorkflowExecutionContext context
    );
}
