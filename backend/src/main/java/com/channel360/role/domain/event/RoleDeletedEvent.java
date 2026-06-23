package com.channel360.role.domain.event;

import lombok.Getter;

@Getter
public class RoleDeletedEvent {
    private final Long roleId;

    public RoleDeletedEvent(Long roleId) {
        this.roleId = roleId;
    }
}
