package com.flownode.core.execution.resolution;

public enum ResolutionState {

    COMPLETED,
    FAILED,
    SKIPPED;

    public static ResolutionState fromNodeState(Enum<?> nodeState) {

        // It converts a NodeState into a ResolutionState.

        String state = nodeState.name();

        return switch (state) {
            case "COMPLETED" -> COMPLETED;
            case "FAILED" -> FAILED;
            case "SKIPPED" -> SKIPPED;
            default -> null; // Not resolved yet
        };
    }
}
