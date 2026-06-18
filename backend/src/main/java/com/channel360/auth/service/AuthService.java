package com.channel360.auth.service;

import com.channel360.auth.dto.ChangePasswordRequest;
import com.channel360.auth.dto.ForgotPasswordRequest;
import com.channel360.auth.dto.LoginRequest;
import com.channel360.auth.dto.LoginResponse;
import com.channel360.auth.dto.RefreshTokenRequest;
import com.channel360.auth.dto.RegisterRequest;
import com.channel360.auth.dto.ResetPasswordRequest;
import com.channel360.auth.entity.RefreshToken;
import com.channel360.auth.mapper.AuthMapper;
import com.channel360.auth.repository.RefreshTokenRepository;
import com.channel360.common.constants.AppConstants;
import com.channel360.common.exception.BadRequestException;
import com.channel360.common.exception.DuplicateResourceException;
import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.common.security.CustomUserDetails;
import com.channel360.common.security.JwtTokenProvider;
import com.channel360.role.entity.Role;
import com.channel360.role.repository.RoleRepository;
import com.channel360.user.entity.User;
import com.channel360.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Transactional
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthMapper authMapper;

    private final Map<String, String> passwordResetTokens = new HashMap<>();

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       AuthMapper authMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authMapper = authMapper;
    }

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

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), roles);
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

        Role defaultRole = roleRepository.findByName(AppConstants.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", AppConstants.ROLE_USER));

        User saved = userRepository.create(
                user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getPassword(), user.getMobileNumber(), user.getEmployeeId(),
                "ACTIVE", null
        );

        userRepository.assignRoles(saved.getId(), java.util.List.of(defaultRole.getId()), null);
        return userRepository.findById(saved.getId()).orElseThrow();
    }

    @Transactional
    public void logout(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        refreshTokenRepository.revoke(refreshTokenStr);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getId()));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        userRepository.changePassword(userDetails.getId(), passwordEncoder.encode(request.getNewPassword()));
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        String resetToken = UUID.randomUUID().toString();
        passwordResetTokens.put(request.getEmail(), resetToken);

        log.info("Password reset token for {}: {}", request.getEmail(), resetToken);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String email = passwordResetTokens.entrySet().stream()
                .filter(entry -> entry.getValue().equals(request.getToken()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        userRepository.changePassword(user.getId(), passwordEncoder.encode(request.getNewPassword()));

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
            refreshTokenRepository.revoke(request.getRefreshToken());
            throw new BadRequestException("Refresh token has expired");
        }

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", refreshToken.getUserId()));

        refreshTokenRepository.revoke(request.getRefreshToken());

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), roles);
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
        refreshTokenRepository.create(
                tokenStr,
                userId,
                LocalDateTime.now().plusSeconds(
                        jwtTokenProvider.getAccessTokenExpiration() / 1000 * 7)
        );
    }
}
