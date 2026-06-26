package com.channel360.auth.api;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RefreshTokenRequest(
    @NotBlank(message = "Refresh token is required") String refreshToken
) {}
