package com.channel360.user.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateUserRequest(
    @NotBlank @Size(max = 50) String employeeId,
    @NotBlank @Size(max = 100) String firstName,
    @NotBlank @Size(max = 100) String lastName,
    @NotBlank @Email @Size(max = 150) String email,
    @Size(max = 20) String mobileNumber,
    List<Long> roleIds
) {}
