package com.channel360.common.domain;

import java.util.Map;
import java.util.Set;

public enum ApprovalStatus {
    PENDING,
    APPROVED,
    REJECTED;

    private static final Map<ApprovalStatus, Set<ApprovalStatus>> VALID_TRANSITIONS = Map.of(
            PENDING, Set.of(APPROVED, REJECTED),
            APPROVED, Set.of(),
            REJECTED, Set.of()
    );

    public boolean canTransitionTo(ApprovalStatus target) {
        return VALID_TRANSITIONS.getOrDefault(this, Set.of()).contains(target);
    }

    public ApprovalStatus transitionTo(ApprovalStatus target) {
        if (!canTransitionTo(target)) {
            throw new IllegalStateException(
                    "Cannot transition from " + this + " to " + target);
        }
        return target;
    }
}
