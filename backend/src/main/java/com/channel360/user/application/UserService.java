package com.channel360.user.application;

import com.channel360.auth.api.AuthFacade;
import com.channel360.auth.api.AuthUserDto;
import com.channel360.common.exception.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.common.dto.response.PageResponse;
import com.channel360.role.api.RoleFacade;
import com.channel360.role.api.RoleResponse;
import com.channel360.user.api.UserResponse;
import com.channel360.user.api.CreateUserRequest;
import com.channel360.user.api.UpdateUserRequest;
import com.channel360.user.api.UserFilterRequest;
import com.channel360.user.domain.User;
import com.channel360.user.domain.event.RoleAssignedEvent;
import com.channel360.user.domain.event.UserCreatedEvent;
import com.channel360.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 16;

    private final UserRepository userRepository;
    private final RoleFacade roleFacade;
    private final UserMapper userMapper;
    private final AuthFacade authFacade;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

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
        Page<UserResponse> dtoPage = page.map(user -> {
            UserResponse dto = userMapper.toDto(user);
            return populateRoles(dto, user);
        });
        dtoPage.getContent().replaceAll(dto -> populateAuthFields(dto));
        return PageResponse.from(dtoPage);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        UserResponse response = userMapper.toDto(user);
        response = populateRoles(response, user);
        response = populateAuthFields(response);
        return response;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (request.employeeId() != null && userRepository.existsByEmployeeId(request.employeeId())) {
            throw new BadRequestException("Employee ID already in use: " + request.employeeId());
        }
        if (authFacade.existsByEmail(request.email())) {
            throw new BadRequestException("Email already in use: " + request.email());
        }

        userRepository.spSave(null, request.firstName(), request.lastName(),
                request.mobileNumber(), request.employeeId(), "ACTIVE", null, null);

        User user = userRepository.findByEmployeeId(request.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "employeeId", request.employeeId()));

        String encodedPassword = passwordEncoder.encode(generateRandomPassword());
        authFacade.saveAuthUser(request.email(), encodedPassword, user.getId(), null);

        if (request.roleIds() != null && !request.roleIds().isEmpty()) {
            validateRolesExist(request.roleIds());
            String joined = String.join(",", request.roleIds().stream().map(String::valueOf).toList());
            userRepository.spAssignRoles(user.getId(), joined, null);
        }

        eventPublisher.publishEvent(new UserCreatedEvent(user.getId(), request.email()));

        UserResponse response = userMapper.toDto(user);
        response = populateRoles(response, user);
        response = populateAuthFields(response);
        return response;
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        userMapper.updateEntity(request, user);

        userRepository.spSave(id, user.getFirstName(), user.getLastName(),
                user.getMobileNumber(), user.getEmployeeId(),
                user.getStatus(), null, null);

        if (request.roleIds() != null) {
            validateRolesExist(request.roleIds());
            String joined = String.join(",", request.roleIds().stream().map(String::valueOf).toList());
            userRepository.spAssignRoles(id, joined, null);
        }

        User updatedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        UserResponse response = userMapper.toDto(updatedUser);
        response = populateRoles(response, updatedUser);
        response = populateAuthFields(response);
        return response;
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
        userRepository.spSave(id, null, null, null, null, "ACTIVE", null, null);
        User activatedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        UserResponse response = userMapper.toDto(activatedUser);
        response = populateRoles(response, activatedUser);
        response = populateAuthFields(response);
        return response;
    }

    @Transactional
    public UserResponse deactivateUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.spSave(id, null, null, null, null, "INACTIVE", null, null);
        User deactivatedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        UserResponse response = userMapper.toDto(deactivatedUser);
        response = populateRoles(response, deactivatedUser);
        response = populateAuthFields(response);
        return response;
    }

    @Transactional
    public UserResponse assignRoles(Long id, List<Long> roleIds) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        validateRolesExist(roleIds);
        String joined = String.join(",", roleIds.stream().map(String::valueOf).toList());
        userRepository.spAssignRoles(id, joined, null);
        User updatedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        UserResponse response = userMapper.toDto(updatedUser);
        response = populateRoles(response, updatedUser);
        response = populateAuthFields(response);

        eventPublisher.publishEvent(new RoleAssignedEvent(id, joined));
        return response;
    }

    @Transactional
    public String resetPassword(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        var newPassword = generateRandomPassword();
        authFacade.changePassword(id, passwordEncoder.encode(newPassword));
        log.info("Password reset for user {}", id);
        return newPassword;
    }

    private UserResponse populateRoles(UserResponse response, User user) {
        try {
            Set<RoleResponse> roleResponses = user.getRoles().stream()
                    .map(role -> roleFacade.getById(role.getId()))
                    .collect(Collectors.toSet());
            return response.toBuilder().roles(roleResponses).build();
        } catch (Exception e) {
            log.warn("Failed to populate roles for user {}: {}", user.getId(), e.getMessage());
            return response;
        }
    }

    private UserResponse populateAuthFields(UserResponse response) {
        try {
            AuthUserDto authDto = authFacade.getAuthById(response.id());
            return response.toBuilder()
                    .email(authDto.email())
                    .lastLoginAt(authDto.lastLoginAt())
                    .build();
        } catch (Exception e) {
            log.warn("Failed to populate auth fields for user {}: {}", response.id(), e.getMessage());
            return response;
        }
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
            if (!roleFacade.existsById(roleId)) {
                throw new ResourceNotFoundException("Role", "id", roleId);
            }
        }
    }
}
