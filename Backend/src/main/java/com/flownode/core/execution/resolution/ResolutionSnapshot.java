package com.flownode.core.execution.resolution;

import java.util.EnumSet;
import java.util.Set;

public class ResolutionSnapshot {

    private int completedCount = 0;
    private int failedCount = 0;
    private int skippedCount = 0;
    private int resolvedCount = 0;

    public void addState(ResolutionState state) {

        if (state == null) return;

        resolvedCount++;

        switch (state) {
            case COMPLETED -> completedCount++;
            case FAILED -> failedCount++;
            case SKIPPED -> skippedCount++;
        }
    }

    public boolean allCompleted(int totalParents) {
        return completedCount == totalParents;
    }

    public boolean allResolved(int totalParents) {
        return resolvedCount == totalParents;
    }

    public boolean anyCompleted() {
        return completedCount > 0;
    }

    public boolean noneFailed() {
        return failedCount == 0;
    }

    // Getters (optional for debugging)
}
