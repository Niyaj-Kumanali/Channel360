package com.channel360.auth.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ChangePasswordRequest(
    @NotBlank(message = "Old password is required") String oldPassword,
    @NotBlank(message = "New password is required") @Size(min = 6, message = "New password must be at least 6 characters") String newPassword
) {}
