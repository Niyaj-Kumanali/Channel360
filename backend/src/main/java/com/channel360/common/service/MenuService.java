package com.channel360.common.service;

import com.channel360.common.dto.response.MenuItem;
import com.channel360.common.security.CustomUserDetails;
import com.channel360.menu.api.MenuRequest;
import com.channel360.menu.api.MenuResponse;
import com.channel360.menu.api.MenuWithPermissions;
import com.channel360.menu.api.PermissionItem;
import com.channel360.menu.infrastructure.MenuItemRepository;
import com.channel360.role.infrastructure.PermissionRepository;
import com.channel360.user.infrastructure.UserRepository;
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
public class MenuService {

    private static final Logger log = LoggerFactory.getLogger(MenuService.class);

    private final MenuItemRepository menuItemRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    // --- Public menu (role-based filtering for sidebar) ---

    public List<MenuItem> getCurrentUserMenu() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() instanceof String) {
            return List.of();
        }

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        List<Long> userRoleIds = userRepository.findRoleIdsByUserId(userDetails.getId());

        if (userRoleIds == null || userRoleIds.isEmpty()) {
            return List.of();
        }

        Set<Long> visibleIds = new HashSet<>(menuItemRepository.findMenuItemIdsByRoleIds(userRoleIds));

        List<com.channel360.menu.domain.MenuItem> parentItems =
                menuItemRepository.findByParentIdIsNullAndActiveTrueOrderByDisplayOrder();

        List<MenuItem> result = new ArrayList<>();
        for (com.channel360.menu.domain.MenuItem parent : parentItems) {
            List<com.channel360.menu.domain.MenuItem> children =
                    menuItemRepository.findByParentIdAndActiveTrueOrderByDisplayOrder(parent.getId());
            List<MenuItem> childItems = children.stream()
                    .filter(c -> visibleIds.contains(c.getId()))
                    .map(this::toMenuItemDto)
                    .toList();

            boolean parentVisible = visibleIds.contains(parent.getId()) || !childItems.isEmpty();
            if (!parentVisible) continue;

            MenuItem dto = toMenuItemDto(parent);
            if (!childItems.isEmpty()) {
                dto.setChildren(new ArrayList<>(childItems));
            }
            result.add(dto);
        }

        return result;
    }

    // --- Admin CRUD ---

    public List<MenuResponse> getAllMenuItems() {
        List<com.channel360.menu.domain.MenuItem> all = menuItemRepository.findAllByOrderByDisplayOrder();
        return all.stream().map(this::toMenuResponse).toList();
    }

    public MenuResponse getMenuItem(Long id) {
        com.channel360.menu.domain.MenuItem entity = menuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Menu item not found: " + id));
        return toMenuResponse(entity);
    }

    @Transactional
    public MenuResponse createMenuItem(MenuRequest request) {
        com.channel360.menu.domain.MenuItem entity = new com.channel360.menu.domain.MenuItem();
        applyRequest(entity, request);
        entity.setActive(request.getActive() != null ? request.getActive() : true);
        entity.setPermissionName(request.getPermissionName());
        com.channel360.menu.domain.MenuItem saved = menuItemRepository.save(entity);
        return toMenuResponse(saved);
    }

    @Transactional
    public MenuResponse updateMenuItem(Long id, MenuRequest request) {
        com.channel360.menu.domain.MenuItem entity = menuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Menu item not found: " + id));
        applyRequest(entity, request);
        com.channel360.menu.domain.MenuItem saved = menuItemRepository.save(entity);
        return toMenuResponse(saved);
    }

    @Transactional
    public void deleteMenuItem(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new EntityNotFoundException("Menu item not found: " + id);
        }
        menuItemRepository.deleteById(id);
    }

    @Transactional
    public void reorderMenuItems(List<MenuRequest> items) {
        for (MenuRequest item : items) {
            if (item.getId() == null) continue;
            menuItemRepository.findById(item.getId()).ifPresent(entity -> {
                entity.setDisplayOrder(item.getDisplayOrder());
                entity.setParentId(item.getParentId());
                menuItemRepository.save(entity);
            });
        }
    }

    // --- Menus with nested permissions ---

    @Transactional(readOnly = true)
    public List<MenuWithPermissions> getMenusWithPermissions() {
        return menuItemRepository.findAllByOrderByDisplayOrder().stream()
                .map(item -> {
                    List<PermissionItem> perms = permissionRepository.findByMenuId(item.getId())
                            .stream()
                            .map(p -> PermissionItem.builder()
                                    .id(p.getId())
                                    .name(p.getName())
                                    .description(p.getDescription())
                                    .module(p.getModule())
                                    .build())
                            .toList();
                    return MenuWithPermissions.builder()
                            .id(item.getId())
                            .parentId(item.getParentId())
                            .label(item.getLabel())
                            .path(item.getPath())
                            .icon(item.getIcon())
                            .displayOrder(item.getDisplayOrder())
                            .active(item.getActive())
                            .permissionName(item.getPermissionName())
                            .permissions(perms)
                            .build();
                })
                .toList();
    }

    // --- Helpers ---

    private MenuItem toMenuItemDto(com.channel360.menu.domain.MenuItem entity) {
        return MenuItem.builder()
                .path(entity.getPath())
                .label(entity.getLabel())
                .icon(entity.getIcon())
                .roles(List.of())
                .build();
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
        else entity.setParentId(null);
        if (request.getDisplayOrder() != null) entity.setDisplayOrder(request.getDisplayOrder());
        if (request.getActive() != null) entity.setActive(request.getActive());
        if (request.getPermissionName() != null) entity.setPermissionName(request.getPermissionName());
    }
}
