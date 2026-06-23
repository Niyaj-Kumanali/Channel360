package com.channel360.user.domain.event;

import lombok.Getter;

@Getter
public class RoleAssignedEvent {
    private final Long userId;
    private final String roleIds;

    public RoleAssignedEvent(Long userId, String roleIds) {
        this.userId = userId;
        this.roleIds = roleIds;
    }
}
