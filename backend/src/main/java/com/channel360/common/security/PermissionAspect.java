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
        Set<String> jwtPermissions = userDetails.getPermissions();

        Set<String> userPermissions = (jwtPermissions != null && !jwtPermissions.isEmpty())
                ? jwtPermissions
                : userRepository.findPermissionNamesByUserId(userDetails.getId());

        // If the required permission isn't in the JWT, try loading from DB as fallback
        if (!userPermissions.contains(requirePermission.value())) {
            Set<String> dbPermissions = userRepository.findPermissionNamesByUserId(userDetails.getId());
            if (dbPermissions.contains(requirePermission.value())) {
                userPermissions = dbPermissions;
            }
        }

        if (!userPermissions.contains(requirePermission.value())) {
            log.warn("Access denied for user {}: missing permission '{}' (has: {})",
                    userDetails.getId(), requirePermission.value(), userPermissions);
            throw new AccessDeniedException("Access denied. Required permission: " + requirePermission.value());
        }

        return pjp.proceed();
    }
}
