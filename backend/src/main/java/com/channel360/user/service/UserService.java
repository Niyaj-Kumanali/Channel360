package com.channel360.user.service;

import com.channel360.common.exception.BadRequestException;
import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.common.dto.response.PageResponse;
import com.channel360.role.repository.RoleRepository;
import com.channel360.user.dto.response.UserResponse;
import com.channel360.user.dto.request.CreateUserRequest;
import com.channel360.user.dto.request.UpdateUserRequest;
import com.channel360.user.dto.request.UserFilterRequest;
import com.channel360.user.entity.User;
import com.channel360.user.mapper.UserMapper;
import com.channel360.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 16;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private String toSnakeCase(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    public PageResponse<UserResponse> getAllUsers(UserFilterRequest filter) {
        String sortBy = toSnakeCase(filter.getSortBy());

        List<User> users = userRepository.spList(
                filter.getSearch(), filter.getStatus(), filter.getRoleId(),
                filter.getPage(), filter.getSize(), sortBy, filter.getSortDir()
        );
        long totalCount = userRepository.spCount(
                filter.getSearch(), filter.getStatus(), filter.getRoleId()
        );

        Page<User> page = new PageImpl<>(users, PageRequest.of(filter.getPage(), filter.getSize()), totalCount);
        Page<UserResponse> dtoPage = page.map(userMapper::toDto);
        return PageResponse.from(dtoPage);
    }

    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use: " + request.getEmail());
        }
        if (request.getEmployeeId() != null && userRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new BadRequestException("Employee ID already in use: " + request.getEmployeeId());
        }

        String encodedPassword = passwordEncoder.encode(generateRandomPassword());
        userRepository.spSave(null, request.getFirstName(), request.getLastName(),
                request.getEmail(), encodedPassword, request.getMobileNumber(),
                request.getEmployeeId(), "ACTIVE", null, null);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            validateRolesExist(request.getRoleIds());
            String joined = String.join(",", request.getRoleIds().stream().map(String::valueOf).toList());
            userRepository.spAssignRoles(user.getId(), joined, null);
        }

        return userMapper.toDto(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        userMapper.updateEntity(request, user);

        userRepository.spSave(id, user.getFirstName(), user.getLastName(), user.getEmail(),
                null, user.getMobileNumber(), user.getEmployeeId(),
                user.getStatus(), null, null);

        if (request.getRoleIds() != null) {
            validateRolesExist(request.getRoleIds());
            String joined = String.join(",", request.getRoleIds().stream().map(String::valueOf).toList());
            userRepository.spAssignRoles(id, joined, null);
        }

        return userMapper.toDto(userRepository.findById(id).orElseThrow());
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.spDelete(id);
    }

    @Transactional
    public UserResponse activateUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.spSave(id, null, null, null, null, null, null, "ACTIVE", null, null);
        return userMapper.toDto(userRepository.findById(id).orElseThrow());
    }

    @Transactional
    public UserResponse deactivateUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.spSave(id, null, null, null, null, null, null, "INACTIVE", null, null);
        return userMapper.toDto(userRepository.findById(id).orElseThrow());
    }

    @Transactional
    public UserResponse assignRoles(Long id, List<Long> roleIds) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        validateRolesExist(roleIds);
        String joined = String.join(",", roleIds.stream().map(String::valueOf).toList());
        userRepository.spAssignRoles(id, joined, null);
        return userMapper.toDto(userRepository.findById(id).orElseThrow());
    }

    @Transactional
    public void resetPassword(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        String newPassword = passwordEncoder.encode(generateRandomPassword());
        userRepository.spChangePassword(id, newPassword);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    private String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }

    private void validateRolesExist(List<Long> roleIds) {
        for (Long roleId : roleIds) {
            if (!roleRepository.existsById(roleId)) {
                throw new ResourceNotFoundException("Role", "id", roleId);
            }
        }
    }
}
