package com.channel360.user.domain.event;

import lombok.Getter;

@Getter
public class UserCreatedEvent {
    private final Long userId;
    private final String email;

    public UserCreatedEvent(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }
}
