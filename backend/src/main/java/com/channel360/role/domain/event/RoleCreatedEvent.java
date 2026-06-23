package com.channel360.role.domain.event;

import com.channel360.role.domain.Role;
import lombok.Getter;

@Getter
public class RoleCreatedEvent {
    private final Long roleId;
    private final String roleName;

    public RoleCreatedEvent(Role role) {
        this.roleId = role.getId();
        this.roleName = role.getName();
    }
}
