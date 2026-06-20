package com.channel360.common.security;

import com.channel360.user.repository.UserRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Aspect
@Component
public class PermissionAspect {

    private static final Logger log = LoggerFactory.getLogger(PermissionAspect.class);

    private final UserRepository userRepository;

    public PermissionAspect(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint pjp, RequirePermission requirePermission) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() instanceof String) {
            log.warn("Access denied: no authenticated user");
            throw new AccessDeniedException("Access denied");
        }

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Set<String> userPermissions = userDetails.getPermissions();

        if (userPermissions == null || userPermissions.isEmpty()) {
            log.info("No permissions in JWT for user {}, loading from DB", userDetails.getId());
            userPermissions = userRepository.findPermissionNamesByUserId(userDetails.getId());
            log.info("DB permissions for user {}: {}", userDetails.getId(), userPermissions);
        } else {
            log.info("JWT permissions for user {}: {}", userDetails.getId(), userPermissions);
        }

        if (!userPermissions.contains(requirePermission.value())) {
            log.warn("Access denied for user {}: missing permission '{}' (has: {})",
                    userDetails.getId(), requirePermission.value(), userPermissions);
            throw new AccessDeniedException("Access denied. Required permission: " + requirePermission.value());
        }

        return pjp.proceed();
    }
}
