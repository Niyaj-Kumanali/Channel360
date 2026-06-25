package com.channel360.auth.application;

import com.channel360.auth.api.*;
import com.channel360.auth.domain.AuthUser;
import com.channel360.auth.domain.RefreshToken;
import com.channel360.auth.infrastructure.AuthUserRepository;
import com.channel360.auth.infrastructure.RefreshTokenRepository;
import com.channel360.common.constants.AppConstants;
import com.channel360.common.exception.BadRequestException;
import com.channel360.common.exception.DuplicateResourceException;
import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.common.security.CustomUserDetails;
import com.channel360.common.security.JwtTokenProvider;
import com.channel360.common.service.EmailService;
import com.channel360.role.api.RoleFacade;
import com.channel360.role.api.RoleResponse;
import com.channel360.user.api.UserFacade;
import com.channel360.user.api.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {


    private static final long RESET_TOKEN_EXPIRY_MINUTES = 30;

    private record ResetTokenEntry(String token, LocalDateTime expiry) {
        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiry);
        }
    }

    private final UserFacade userFacade;
    private final RoleFacade roleFacade;
    private final AuthUserRepository authUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final Map<String, ResetTokenEntry> passwordResetTokens = new HashMap<>();

    @Transactional
    public LoginResponse login(LoginRequest request) {
        AuthUser authUser = authUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        AuthUserDto user = toAuthDto(authUser);

        if (!passwordEncoder.matches(request.getPassword(), user.password())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (user.deletedFlag()) {
            throw new BadCredentialsException("Account has been deactivated");
        }

        var roleNames = userFacade.findRoleNamesByUserId(user.id());
        var permissionNames = userFacade.findPermissionNamesByUserId(user.id());

        String accessToken = jwtTokenProvider.generateAccessToken(user.id(), roleNames, permissionNames);
        String refreshTokenStr = jwtTokenProvider.generateRefreshToken(user.id());

        saveRefreshToken(user.id(), refreshTokenStr);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .tokenType(AppConstants.TOKEN_TYPE)
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .build();
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (authUserRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Long userId = userFacade.saveUser(request.getFirstName(), request.getLastName(),
                request.getMobileNumber(), request.getEmployeeId(), "ACTIVE", null, null);

        AuthUser user = AuthUser.builder()
                .id(userId)
                .email(request.getEmail())
                .password(encodedPassword)
                .build();
        authUserRepository.save(user);

        RoleResponse defaultRole = roleFacade.findByName("ROLE_GUEST");
        userFacade.assignRoles(userId, String.valueOf(defaultRole.getId()), null);
        return userFacade.getById(userId);
    }

    @Transactional
    public void logout(String refreshTokenStr) {
        refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));
        refreshTokenRepository.spRevoke(refreshTokenStr);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        userFacade.getById(userDetails.getId());

        if (!passwordEncoder.matches(request.getOldPassword(), userDetails.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        authUserRepository.spChangePassword(userDetails.getId(), passwordEncoder.encode(request.getNewPassword()));
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        try {
            authUserRepository.findByEmail(request.getEmail());
            String resetToken = UUID.randomUUID().toString();
            passwordResetTokens.put(request.getEmail(),
                    new ResetTokenEntry(resetToken, LocalDateTime.now().plusMinutes(RESET_TOKEN_EXPIRY_MINUTES)));

            String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

            emailService.sendResetPasswordEmail(request.getEmail(), resetLink);

            log.info("Password reset email sent to: {}", request.getEmail());
        } catch (Exception e) {
            log.info("Password reset requested for non-existent email: {}", request.getEmail());
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        ResetTokenEntry entry = passwordResetTokens.values().stream()
                .filter(resetTokenEntry -> resetTokenEntry.token().equals(request.getToken()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

        if (entry.isExpired()) {
            passwordResetTokens.values().remove(entry);
            throw new BadRequestException("Invalid or expired reset token");
        }

        String email = passwordResetTokens.entrySet().stream()
                .filter(e -> e.getValue().token().equals(request.getToken()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

        try {
            AuthUser authUser = authUserRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
            authUserRepository.spChangePassword(authUser.getId(), passwordEncoder.encode(request.getNewPassword()));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to reset password for email {}: {}", email, e.getMessage());
            throw new ResourceNotFoundException("User", "email", email);
        }

        passwordResetTokens.remove(email);
    }

    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("No authenticated user found");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userFacade.getById(userDetails.getId());
    }

    public LoginResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new BadRequestException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.spRevoke(request.getRefreshToken());
            throw new BadRequestException("Refresh token has expired");
        }

        AuthUser authUserForRefresh = authUserRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", refreshToken.getUserId()));
        AuthUserDto user = toAuthDto(authUserForRefresh);

        refreshTokenRepository.spRevoke(request.getRefreshToken());

        var roleNames = userFacade.findRoleNamesByUserId(user.id());
        var permissionNames = userFacade.findPermissionNamesByUserId(user.id());

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.id(), roleNames, permissionNames);
        String newRefreshTokenStr = jwtTokenProvider.generateRefreshToken(user.id());

        saveRefreshToken(user.id(), newRefreshTokenStr);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshTokenStr)
                .tokenType(AppConstants.TOKEN_TYPE)
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .build();
    }

    private void saveRefreshToken(Long userId, String tokenStr) {
        refreshTokenRepository.spSave(null, tokenStr, userId,
                LocalDateTime.now().plusSeconds(jwtTokenProvider.getAccessTokenExpiration() / 1000 * 7),
                false);
    }

    private AuthUserDto toAuthDto(AuthUser authUser) {
        return AuthUserDto.builder()
                .id(authUser.getId())
                .email(authUser.getEmail())
                .password(authUser.getPassword())
                .deletedFlag(authUser.isDeletedFlag())
                .lastLoginAt(authUser.getLastLoginAt())
                .build();
    }
}
