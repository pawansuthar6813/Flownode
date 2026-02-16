package com.flownode.core.execution.context;

import com.flownode.core.state.NodeState;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WorkflowExecutionContext {

    private final Map<String, Object> data = new HashMap<>();

    private final Map<String, NodeState> nodeStates = new HashMap<>();  // Map<nodeId, NodeState>

    public void put(String key, Object value){
        data.put(key, value);
    }

    public Object get(String key){
        return data.get(key);
    }

    public boolean contains(String key) {
        return data.containsKey(key);
    }

    public Map<String, Object> getAll() {
        return Collections.unmodifiableMap(data);
    }

    public void setNodeState(String nodeId, NodeState state) {
        nodeStates.put(nodeId, state);
    }

    public NodeState getNodeState(String nodeId) {
        return nodeStates.getOrDefault(nodeId, NodeState.PENDING);
    }

}
