package com.flownode.core.execution.readiness;

import com.flownode.core.execution.context.WorkflowExecutionContext;
import com.flownode.core.execution.resolution.ResolutionSnapshot;
import com.flownode.core.execution.resolution.ResolutionState;
import com.flownode.core.execution.resolution.policy.AllCompletedPolicy;
import com.flownode.core.execution.resolution.policy.AllResolvedPolicy;
import com.flownode.core.execution.resolution.policy.ResolutionPolicy;
import com.flownode.core.state.NodeState;
import com.flownode.core.workflow.Workflow;
import org.hibernate.jdbc.Work;

import java.util.List;

public class DefaultJoinReadinessEvaluator implements JoinReadinessEvaluator{


    @Override
    public boolean isNodeReady(String nodeId, Workflow workflow, WorkflowExecutionContext context) {

        List<String> parentNodesId = workflow.getParents(nodeId);

        // start node case
        if(parentNodesId == null || parentNodesId.isEmpty()){
            return true;
        }

        ResolutionSnapshot snapshot = new ResolutionSnapshot();

        for(String parentId : parentNodesId){

            NodeState parentState = context.getNodeState(parentId);

            ResolutionState rs = ResolutionState.fromNodeState(parentState);

            snapshot.addState(rs);

        }

        ResolutionPolicy policy = getResolutionPolicy(nodeId, workflow);

        return policy.isSatisfied(snapshot, parentNodesId.size());
    }

//    private boolean isResolved(NodeState state){
//        return state == NodeState.COMPLETED || state == NodeState.SKIPPED;
//    }

    private ResolutionPolicy getResolutionPolicy(String nodeId, Workflow workflow){
        // default Resolution policy
        return new AllResolvedPolicy();

        // Later:
        // return workflow.getNode(nodeId)
        //        .map(Node::getResolutionPolicy)
        //        .orElse(new AllCompletedPolicy());
    }
}
