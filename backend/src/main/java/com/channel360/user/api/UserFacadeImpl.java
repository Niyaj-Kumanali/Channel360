package com.channel360.user.api;

import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.user.domain.User;
import com.channel360.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
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
                .orElseThrow(() -> new ResourceNotFoundException("User", "employeeId", employeeId));
    }

    @Override
    public Long saveUser(String firstName, String lastName, String mobileNumber,
                          String employeeId, String status, String createdBy, String modifiedBy) {
        userRepository.spSave(null, firstName, lastName, mobileNumber,
                employeeId, status, createdBy, modifiedBy);
        if (employeeId != null) {
            return userRepository.findByEmployeeId(employeeId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "employeeId", employeeId))
                    .getId();
        }
        return userRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", null))
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
