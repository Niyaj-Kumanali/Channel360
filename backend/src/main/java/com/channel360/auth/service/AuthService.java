package com.channel360.auth.service;

import com.channel360.auth.dto.request.ChangePasswordRequest;
import com.channel360.auth.dto.request.ForgotPasswordRequest;
import com.channel360.auth.dto.request.LoginRequest;
import com.channel360.auth.dto.request.RefreshTokenRequest;
import com.channel360.auth.dto.request.RegisterRequest;
import com.channel360.auth.dto.request.ResetPasswordRequest;
import com.channel360.auth.dto.response.LoginResponse;
import com.channel360.auth.entity.RefreshToken;
import com.channel360.auth.mapper.AuthMapper;
import com.channel360.auth.repository.RefreshTokenRepository;
import com.channel360.common.constants.AppConstants;
import com.channel360.common.exception.BadRequestException;
import com.channel360.common.exception.DuplicateResourceException;
import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.common.security.CustomUserDetails;
import com.channel360.common.security.JwtTokenProvider;
import com.channel360.common.service.EmailService;
import com.channel360.role.entity.Permission;
import com.channel360.role.entity.Role;
import com.channel360.role.repository.RoleRepository;
import com.channel360.user.entity.User;
import com.channel360.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private static final long RESET_TOKEN_EXPIRY_MINUTES = 30;

    private record ResetTokenEntry(String token, LocalDateTime expiry) {
        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiry);
        }
    }

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthMapper authMapper;
    private final EmailService emailService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final Map<String, ResetTokenEntry> passwordResetTokens = new HashMap<>();

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (user.isDeletedFlag()) {
            throw new BadCredentialsException("Account has been deactivated");
        }

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        Set<String> permissionNames = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), roles, permissionNames);
        String refreshTokenStr = jwtTokenProvider.generateRefreshToken(user.getId());

        saveRefreshToken(user.getId(), refreshTokenStr);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .tokenType(AppConstants.TOKEN_TYPE)
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .build();
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        User user = authMapper.registerRequestToUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role defaultRole = roleRepository.findByName("ROLE_GUEST")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_GUEST"));

        userRepository.spSave(null, user.getFirstName(), user.getLastName(),
                user.getEmail(), user.getPassword(), user.getMobileNumber(),
                user.getEmployeeId(), "ACTIVE", null, null);

        User savedUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", user.getEmail()));

        userRepository.spAssignRoles(savedUser.getId(), String.valueOf(defaultRole.getId()), null);
        return savedUser;
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

        userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getId()));

        if (!passwordEncoder.matches(request.getOldPassword(), userDetails.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        userRepository.spChangePassword(userDetails.getId(), passwordEncoder.encode(request.getNewPassword()));
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            String resetToken = UUID.randomUUID().toString();
            passwordResetTokens.put(request.getEmail(),
                    new ResetTokenEntry(resetToken, LocalDateTime.now().plusMinutes(RESET_TOKEN_EXPIRY_MINUTES)));

            String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

            emailService.sendResetPasswordEmail(request.getEmail(), resetLink);

            log.info("Password reset email sent to: {}", request.getEmail());
        });
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

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        userRepository.spChangePassword(user.getId(), passwordEncoder.encode(request.getNewPassword()));

        passwordResetTokens.remove(email);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("No authenticated user found");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getId()));
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

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", refreshToken.getUserId()));

        refreshTokenRepository.spRevoke(request.getRefreshToken());

        Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

        Set<String> permissionNames = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), roles, permissionNames);
        String newRefreshTokenStr = jwtTokenProvider.generateRefreshToken(user.getId());

        saveRefreshToken(user.getId(), newRefreshTokenStr);

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
}
