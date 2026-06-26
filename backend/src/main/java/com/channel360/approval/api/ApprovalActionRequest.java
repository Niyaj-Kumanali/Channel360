package com.channel360.approval.api;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ApprovalActionRequest(
    @NotNull(message = "User ID is required") Long userId,
    String comments
) {}
