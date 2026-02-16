package com.flownode.core.execution.readiness;

import com.flownode.core.execution.context.WorkflowExecutionContext;
import com.flownode.core.state.NodeState;
import com.flownode.core.workflow.Workflow;

import java.util.List;

public class DefaultJoinReadinessEvaluator implements JoinReadinessEvaluator{


    @Override
    public boolean isNodeReady(String nodeId, Workflow workflow, WorkflowExecutionContext context) {

        List<String> parentNodesId = workflow.getParents(nodeId);

        if(parentNodesId.isEmpty()){
            return true;
        }

        for(String parentId : parentNodesId){

            NodeState state = context.getNodeState(parentId);
            if(!isResolved(state)) return false;
        }

        return true;
    }

    private boolean isResolved(NodeState state){
        return state == NodeState.COMPLETED || state == NodeState.SKIPPED;
    }
}
