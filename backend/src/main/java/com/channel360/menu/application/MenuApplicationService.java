package com.channel360.menu.application;

import com.channel360.common.dto.response.MenuItem;
import com.channel360.common.security.CustomUserDetails;
import com.channel360.menu.api.MenuFacade;
import com.channel360.menu.api.MenuRequest;
import com.channel360.menu.api.MenuResponse;
import com.channel360.menu.api.MenuWithPermissions;
import com.channel360.menu.api.PermissionItem;
import com.channel360.role.api.RoleFacade;
import com.channel360.user.api.UserFacade;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MenuApplicationService {

    private static final Logger log = LoggerFactory.getLogger(MenuApplicationService.class);

    private final MenuFacade menuFacade;
    private final RoleFacade roleFacade;
    private final UserFacade userFacade;

    public List<MenuItem> getCurrentUserMenu() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return List.of();
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<Long> roleIds = userFacade.findRoleIdsByUserId(userDetails.getId());

        if (roleIds.isEmpty()) {
            return List.of();
        }

        Set<Long> visibleIds = new HashSet<>(menuFacade.findMenuItemIdsByRoleIds(roleIds));

        List<MenuItem> menuItems = new ArrayList<>();
        List<com.channel360.menu.domain.MenuItem> rootItems = menuFacade.findRootMenuItems();

        for (com.channel360.menu.domain.MenuItem parent : rootItems) {
            if (visibleIds.contains(parent.getId())) {
                List<com.channel360.menu.domain.MenuItem> children = menuFacade.findChildMenuItems(parent.getId())
                        .stream()
                        .filter(child -> visibleIds.contains(child.getId()))
                        .toList();

                menuItems.add(MenuItem.builder()
                        .path(parent.getPath())
                        .label(parent.getLabel())
                        .icon(parent.getIcon())
                        .roles(List.of())
                        .children(children.stream()
                                .map(child -> MenuItem.builder()
                                        .path(child.getPath())
                                        .label(child.getLabel())
                                        .icon(child.getIcon())
                                        .roles(List.of())
                                        .build())
                                .toList())
                        .build());
            }
        }

        return menuItems;
    }

    public List<MenuResponse> getAllMenuItems() {
        return menuFacade.findAllOrdered().stream()
                .map(this::toMenuResponse)
                .toList();
    }

    public MenuResponse getMenuItemById(Long id) {
        com.channel360.menu.domain.MenuItem item = menuFacade.findById(id);
        return toMenuResponse(item);
    }

    @Transactional
    public MenuResponse createMenuItem(MenuRequest request) {
        com.channel360.menu.domain.MenuItem entity = new com.channel360.menu.domain.MenuItem();
        applyRequest(entity, request);
        com.channel360.menu.domain.MenuItem saved = menuFacade.save(entity);
        return toMenuResponse(saved);
    }

    @Transactional
    public MenuResponse updateMenuItem(Long id, MenuRequest request) {
        com.channel360.menu.domain.MenuItem entity = menuFacade.findById(id);
        applyRequest(entity, request);
        com.channel360.menu.domain.MenuItem saved = menuFacade.save(entity);
        return toMenuResponse(saved);
    }

    @Transactional
    public void deleteMenuItem(Long id) {
        if (!menuFacade.existsById(id)) {
            throw new EntityNotFoundException("Menu item not found with id: " + id);
        }
        menuFacade.deleteById(id);
    }

    @Transactional
    public void reorderMenu(Long id, Integer newOrder) {
        com.channel360.menu.domain.MenuItem entity = menuFacade.findById(id);
        entity.setDisplayOrder(newOrder);
        menuFacade.save(entity);
    }

    public List<MenuWithPermissions> getMenusWithPermissions() {
        return menuFacade.findAllOrdered().stream()
                .map(item -> {
                    List<PermissionItem> permissions = roleFacade.findPermissionsByMenuId(item.getId());
                    return MenuWithPermissions.builder()
                            .id(item.getId())
                            .parentId(item.getParentId())
                            .label(item.getLabel())
                            .path(item.getPath())
                            .icon(item.getIcon())
                            .permissionName(item.getPermissionName())
                            .displayOrder(item.getDisplayOrder())
                            .active(item.getActive())
                            .permissions(permissions)
                            .build();
                })
                .toList();
    }

    private MenuResponse toMenuResponse(com.channel360.menu.domain.MenuItem entity) {
        return MenuResponse.builder()
                .id(entity.getId())
                .parentId(entity.getParentId())
                .label(entity.getLabel())
                .path(entity.getPath())
                .icon(entity.getIcon())
                .permissionName(entity.getPermissionName())
                .displayOrder(entity.getDisplayOrder())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private void applyRequest(com.channel360.menu.domain.MenuItem entity, MenuRequest request) {
        if (request.getLabel() != null) entity.setLabel(request.getLabel());
        if (request.getPath() != null) entity.setPath(request.getPath());
        if (request.getIcon() != null) entity.setIcon(request.getIcon());
        if (request.getParentId() != null) entity.setParentId(request.getParentId());
        if (request.getPermissionName() != null) entity.setPermissionName(request.getPermissionName());
        if (request.getDisplayOrder() != null) entity.setDisplayOrder(request.getDisplayOrder());
        if (request.getActive() != null) entity.setActive(request.getActive());
    }
}
