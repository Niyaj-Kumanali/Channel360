package com.channel360.common.service;

import com.channel360.common.dto.MenuItem;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MenuService {

    public List<MenuItem> getCurrentUserMenu() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() instanceof String) {
            return List.of();
        }

        Set<String> userRoles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return buildMenu().stream()
                .filter(item -> hasAccess(item, userRoles))
                .map(item -> filterChildren(item, userRoles))
                .toList();
    }

    private List<MenuItem> buildMenu() {
        MenuItem dashboard = MenuItem.builder()
                .path("/dashboard")
                .label("Dashboard")
                .icon("LayoutDashboard")
                .roles(List.of("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_USER"))
                .build();

        MenuItem users = MenuItem.builder()
                .path("/users")
                .label("Users")
                .icon("Users")
                .roles(List.of("ROLE_SUPER_ADMIN", "ROLE_ADMIN"))
                .build();

        return List.of(dashboard, users);
    }

    private boolean hasAccess(MenuItem item, Set<String> userRoles) {
        return item.getRoles().stream().anyMatch(userRoles::contains);
    }

    private MenuItem filterChildren(MenuItem item, Set<String> userRoles) {
        if (item.getChildren() == null || item.getChildren().isEmpty()) {
            return item;
        }
        item.setChildren(item.getChildren().stream()
                .filter(child -> hasAccess(child, userRoles))
                .collect(Collectors.toList()));
        return item;
    }
}
