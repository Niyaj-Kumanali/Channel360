package com.channel360.auth.api;

import lombok.Builder;

@Builder
public record LoginResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    Long expiresIn
) {}