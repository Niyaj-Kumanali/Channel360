package com.channel360.user.api;

import com.channel360.user.api.response.UserResponse;

import java.util.List;
import java.util.Set;

public interface UserFacade {
    UserResponse getById(Long id);
    List<Long> findRoleIdsByUserId(Long userId);
    Set<String> findRoleNamesByUserId(Long userId);
    Set<String> findPermissionNamesByUserId(Long userId);
    boolean existsById(Long id);
    Long findByEmployeeId(String employeeId);
    Long saveUser(String firstName, String lastName, String mobileNumber,
                   String employeeId, String status, String createdBy, String modifiedBy);
    void assignRoles(Long userId, String roleIds, String modifiedBy);
}
