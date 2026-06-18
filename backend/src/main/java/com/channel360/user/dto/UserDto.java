package com.channel360.user.dto;

import com.channel360.role.dto.RoleDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private String status;
    private LocalDateTime lastLoginAt;
    private Set<RoleDto> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
