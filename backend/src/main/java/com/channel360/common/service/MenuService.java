package com.channel360.common.service;

import com.channel360.common.dto.response.MenuItem;
import com.channel360.role.enums.RoleName;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
                .roles(List.of(RoleName.ROLE_SUPER_ADMIN.name(), RoleName.ROLE_ADMIN.name(), RoleName.ROLE_USER.name()))
                .build();


        MenuItem roles = MenuItem.builder()
                .path("/admin/roles")
                .label("Roles")
                .icon("Shield")
                .roles(List.of(RoleName.ROLE_SUPER_ADMIN.name()))
                .build();

        MenuItem cms = MenuItem.builder()
                .path("#")
                .label("Content")
                .icon("FileText")
                .roles(List.of(RoleName.ROLE_SUPER_ADMIN.name(), RoleName.ROLE_ADMIN.name()))
                .children(new ArrayList<>(List.of(
                    MenuItem.builder()
                        .path("/admin/sections")
                        .label("Homepage Sections")
                        .icon("Layout")
                        .roles(List.of(RoleName.ROLE_SUPER_ADMIN.name(), RoleName.ROLE_ADMIN.name()))
                        .build(),
                    MenuItem.builder()
                        .path("/admin/popups")
                        .label("Popups")
                        .icon("Square")
                        .roles(List.of(RoleName.ROLE_SUPER_ADMIN.name(), RoleName.ROLE_ADMIN.name()))
                        .build()
                )))
                .build();

        return List.of(dashboard, roles, cms);
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
