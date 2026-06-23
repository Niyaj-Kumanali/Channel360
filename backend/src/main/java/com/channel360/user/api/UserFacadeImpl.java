package com.channel360.user.api;

import com.channel360.user.domain.User;
import com.channel360.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserRepository userRepository;

    @Override
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return toResponse(user);
    }

    @Override
    public List<Long> findRoleIdsByUserId(Long userId) {
        return userRepository.findRoleIdsByUserId(userId);
    }

    @Override
    public Set<String> findRoleNamesByUserId(Long userId) {
        return userRepository.findRoleNamesByUserId(userId);
    }

    @Override
    public Set<String> findPermissionNamesByUserId(Long userId) {
        return userRepository.findPermissionNamesByUserId(userId);
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public Long findByEmployeeId(String employeeId) {
        return userRepository.findByEmployeeId(employeeId)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found with employee id: " + employeeId));
    }

    @Override
    public Long saveUser(String firstName, String lastName, String mobileNumber,
                          String employeeId, String status, String createdBy, String modifiedBy) {
        userRepository.spSave(null, firstName, lastName, mobileNumber,
                employeeId, status, createdBy, modifiedBy);
        return userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve saved user"))
                .getId();
    }

    @Override
    public void assignRoles(Long userId, String roleIds, String modifiedBy) {
        userRepository.spAssignRoles(userId, roleIds, modifiedBy);
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .employeeId(user.getEmployeeId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .mobileNumber(user.getMobileNumber())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
