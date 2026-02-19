package com.flownode.core.execution.resolution.policy;

import com.flownode.core.execution.resolution.ResolutionSnapshot;

public class AnyCompletedPolicy implements ResolutionPolicy {

    @Override
    public boolean isSatisfied(ResolutionSnapshot snapshot, int totalParents) {
        return snapshot.anyCompleted();
    }
}

