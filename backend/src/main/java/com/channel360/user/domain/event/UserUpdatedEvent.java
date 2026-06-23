package com.channel360.user.domain.event;

import lombok.Getter;

@Getter
public class UserUpdatedEvent {
    private final Long userId;
    private final String email;

    public UserUpdatedEvent(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }
}
