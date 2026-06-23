package com.channel360.role.domain.event;

import com.channel360.role.domain.Role;
import lombok.Getter;

@Getter
public class RoleUpdatedEvent {
    private final Long roleId;
    private final String roleName;

    public RoleUpdatedEvent(Role role) {
        this.roleId = role.getId();
        this.roleName = role.getName();
    }
}
