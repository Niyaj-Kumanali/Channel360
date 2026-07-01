package com.channel360.auth.application;

import com.channel360.auth.api.AuthRoleProvider;
import com.channel360.auth.api.AuthUserProvider;
import com.channel360.auth.api.request.*;
import com.channel360.auth.api.response.LoginResponse;
import com.channel360.auth.domain.PasswordResetToken;
import com.channel360.auth.domain.RefreshToken;
import com.channel360.user.domain.User;
import com.channel360.user.infrastructure.UserRepository;
import com.channel360.auth.infrastructure.PasswordResetTokenRepository;
import com.channel360.auth.infrastructure.RefreshTokenRepository;
import com.channel360.common.exception.BadRequestException;
import com.channel360.common.exception.DuplicateResourceException;
import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.common.security.CustomUserDetails;
import com.channel360.common.security.JwtTokenProvider;
import com.channel360.email.application.EmailService;
import com.channel360.role.api.response.RoleResponse;
import com.channel360.user.api.response.UserResponse;
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
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {


    private static final long RESET_TOKEN_EXPIRY_MINUTES = 30;

    private final AuthUserProvider authUserProvider;
    private final AuthRoleProvider authRoleProvider;


    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User authUser = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), authUser.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (authUser.isDeletedFlag()) {
            throw new BadCredentialsException("Account has been deactivated");
        }

        var roleNames = authUserProvider.findRoleNamesByUserId(authUser.getId());
        var permissionNames = authUserProvider.findPermissionNamesByUserId(authUser.getId());

        String accessToken = jwtTokenProvider.generateAccessToken(authUser.getId(), roleNames, permissionNames);
        String refreshTokenStr = jwtTokenProvider.generateRefreshToken(authUser.getId());

        saveRefreshToken(authUser.getId(), refreshTokenStr);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .build();
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("User", "email", request.email());
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        userRepository.spSave(null, request.email(), encodedPassword, request.firstName(), request.lastName(),
                request.mobileNumber(), request.employeeId(), "ACTIVE", null, null);

        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.email()));
        Long userId = user.getId();

        RoleResponse defaultRole = authRoleProvider.findByName("ROLE_GUEST");
        authUserProvider.assignRoles(userId, List.of(defaultRole.id()));
        return authUserProvider.getById(userId);
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

        authUserProvider.getById(userDetails.getId());

        if (!passwordEncoder.matches(request.oldPassword(), userDetails.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        userRepository.spChangePassword(userDetails.getId(), passwordEncoder.encode(request.newPassword()));
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.email()));

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .email(request.email())
                .expiryDate(LocalDateTime.now().plusMinutes(RESET_TOKEN_EXPIRY_MINUTES))
                .build();
        passwordResetTokenRepository.save(resetToken);

        String resetLink = frontendUrl + "/reset-password?token=" + resetToken.getToken();

        emailService.sendResetPasswordEmail(request.email(), resetLink);

        log.info("Password reset email sent to: {}", request.email());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.token())
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

        if (resetToken.isUsed() || resetToken.isExpired()) {
            throw new BadRequestException("Invalid or expired reset token");
        }

        User authUser = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", resetToken.getEmail()));

        userRepository.spChangePassword(authUser.getId(), passwordEncoder.encode(request.newPassword()));

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("No authenticated user found");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return authUserProvider.getById(userDetails.getId());
    }

    public LoginResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new BadRequestException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.spRevoke(request.refreshToken());
            throw new BadRequestException("Refresh token has expired");
        }

        User authUserForRefresh = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", refreshToken.getUserId()));

        refreshTokenRepository.spRevoke(request.refreshToken());

        var roleNames = authUserProvider.findRoleNamesByUserId(authUserForRefresh.getId());
        var permissionNames = authUserProvider.findPermissionNamesByUserId(authUserForRefresh.getId());

        String newAccessToken = jwtTokenProvider.generateAccessToken(authUserForRefresh.getId(), roleNames, permissionNames);
        String newRefreshTokenStr = jwtTokenProvider.generateRefreshToken(authUserForRefresh.getId());

        saveRefreshToken(authUserForRefresh.getId(), newRefreshTokenStr);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshTokenStr)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .build();
    }

    private void saveRefreshToken(Long userId, String tokenStr) {
        refreshTokenRepository.spSave(null, tokenStr, userId,
                LocalDateTime.now().plusSeconds(jwtTokenProvider.getAccessTokenExpiration() / 1000 * 7),
                false);
    }

}
