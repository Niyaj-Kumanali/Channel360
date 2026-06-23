package com.channel360.user.api;

import com.channel360.user.domain.User;
import com.channel360.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserRepository userRepository;

    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return toResponse(user);
    }

    public AuthUserDto getAuthById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return toAuthDto(user);
    }

    public AuthUserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return toAuthDto(user);
    }

    public List<Long> findRoleIdsByUserId(Long userId) {
        return userRepository.findRoleIdsByUserId(userId);
    }

    public Set<String> findPermissionNamesByUserId(Long userId) {
        return userRepository.findPermissionNamesByUserId(userId);
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Long saveUser(String firstName, String lastName, String email,
                          String password, String mobileNumber, String employeeId,
                          String status, String createdBy, String modifiedBy) {
        userRepository.spSave(null, firstName, lastName, email, password,
                mobileNumber, employeeId, status, createdBy, modifiedBy);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve saved user"))
                .getId();
    }

    public void assignRoles(Long userId, String roleIds, String modifiedBy) {
        userRepository.spAssignRoles(userId, roleIds, modifiedBy);
    }

    public void changePassword(Long userId, String encodedPassword) {
        userRepository.spChangePassword(userId, encodedPassword);
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .employeeId(user.getEmployeeId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .status(user.getStatus())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private AuthUserDto toAuthDto(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());
        Set<String> permissionNames = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getName())
                .collect(Collectors.toSet());
        return AuthUserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .status(user.getStatus())
                .deletedFlag(user.isDeletedFlag())
                .roleNames(roleNames)
                .permissionNames(permissionNames)
                .build();
    }
}
