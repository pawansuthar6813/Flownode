package com.flownode.core.execution.resolution.policy;

import com.flownode.core.execution.resolution.ResolutionSnapshot;

public class AllCompletedPolicy implements ResolutionPolicy {

    @Override
    public boolean isSatisfied(ResolutionSnapshot snapshot, int totalParents) {
        return snapshot.allCompleted(totalParents);
    }
}

// policies
// 1. ALL_COMPLETED = all parents are completed
// 2. ALL_RESOLVED = all parents are either completed or skipped or failed
// 3. ANY_COMPLETED = any of the parent is completed
//

