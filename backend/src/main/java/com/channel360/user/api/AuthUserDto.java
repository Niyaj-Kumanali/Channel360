package com.channel360.user.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserDto {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String status;
    private boolean deletedFlag;
    private Set<String> roleNames;
    private Set<String> permissionNames;
}
