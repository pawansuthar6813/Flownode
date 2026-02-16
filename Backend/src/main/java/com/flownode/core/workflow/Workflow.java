package com.flownode.core.workflow;

import com.flownode.core.execution.node.Node;
import com.flownode.core.state.WorkflowState;

import java.util.*;

public class Workflow {

    private String workflowId;
    private String workflowName;
    private WorkflowState workflowState;


    // Node registry
    private Map<String, Node> nodes = new HashMap<>();   // Map<nodeId, Node>


    // Linear transitions
    private Map<String, List<String>> transitions = new HashMap<>();   // Map<nodeId, nodeIds of next nodes>

    // Condition transitions
    private Map<String, String> trueTransitions = new HashMap<>();    // Map<conditionNodeId, nodeId of TRUE branch next node>
    private Map<String, String> falseTransitions = new HashMap<>();   // Map<conditionNodeId, nodeId of FALSE branch next node>

    // Parent map
    private Map<String, List<String>> parentMap = new HashMap<>();     // Map<childNodeId, nodeIds of all parents>

    private String startNodeId;


    public Workflow(String workflowId, String workflowName) {
        this.workflowId = workflowId;
        this.workflowName = workflowName;
        this.workflowState = WorkflowState.CREATED;
    }


    // ---------------- NODE REGISTRATION ----------------

    public void addNode(Node node) {
        nodes.put(node.getNodeId(), node);
    }


    // ---------------- TRANSITIONS ----------------

    public void addTransition(String fromNodeId, String toNodeId) {
        transitions.computeIfAbsent(fromNodeId, k -> new ArrayList<>()).add(toNodeId);

        addParent(toNodeId, fromNodeId);
    }


    public void addConditionTransition(String conditionNodeId, String trueNodeId, String falseNodeId) {
        trueTransitions.put(conditionNodeId, trueNodeId);
        falseTransitions.put(conditionNodeId, falseNodeId);

        addParent(trueNodeId, conditionNodeId);
        addParent(falseNodeId, conditionNodeId);
    }


    // ---------------- NODE LOOKUP ----------------


    public Optional<Node> getNode(String nodeId) {
        return Optional.ofNullable(nodes.get(nodeId));
    }

    public List<String> getNextNodesId(String nodeId) {
        return transitions.getOrDefault(nodeId, new ArrayList<>());
    }


    public String getTrueNodeId(String nodeId) {
        return trueTransitions.get(nodeId);
    }

    public String getFalseNodeId(String nodeId) {
        return falseTransitions.get(nodeId);
    }


    // ---------------- START NODE ----------------

    public void setStartNodeId(String startNodeId) {
        this.startNodeId = startNodeId;
    }

    public String getStartNodeId() {
        return startNodeId;
    }


    // ---------------- STATE ----------------

    public WorkflowState getWorkflowState() {
        return workflowState;
    }

    public void setWorkflowState(WorkflowState workflowState) {
        this.workflowState = workflowState;
    }



    // ---------------- METADATA ----------------

    public String getWorkflowId() {
        return workflowId;
    }

    public String getWorkflowName() {
        return workflowName;
    }



    // ---------------- PARENT MAP SUPPORT ----------------

    private void addParent(String childNodeId, String parentNodeId) {

        parentMap
                .computeIfAbsent(childNodeId, k -> new ArrayList<>())
                .add(parentNodeId);
    }

    public List<String> getParents(String nodeId) {
        return parentMap.getOrDefault(nodeId, Collections.emptyList());
    }


}