package com.channel360.common.security;

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

    private final SecurityUserProvider securityUserProvider;

    public PermissionAspect(SecurityUserProvider securityUserProvider) {
        this.securityUserProvider = securityUserProvider;
    }

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        String requiredPermission = requirePermission.value();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Set<String> permissions = userDetails.getPermissions();
        if (permissions == null || permissions.isEmpty()) {
            permissions = securityUserProvider.findPermissionNamesByUserId(userDetails.getId());
        }

        if (permissions.contains(requiredPermission)) {
            return joinPoint.proceed();
        }

        log.warn("Access denied for user {}: missing permission '{}'",
                userDetails.getId(), requiredPermission);
        throw new AccessDeniedException("Access denied: missing permission '" + requiredPermission + "'");
    }
}
