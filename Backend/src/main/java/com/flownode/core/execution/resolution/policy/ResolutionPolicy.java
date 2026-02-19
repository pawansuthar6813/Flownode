package com.flownode.core.execution.resolution.policy;

import com.flownode.core.execution.resolution.ResolutionSnapshot;

public interface ResolutionPolicy {

    boolean isSatisfied(ResolutionSnapshot snapshot, int totalParents);
}
