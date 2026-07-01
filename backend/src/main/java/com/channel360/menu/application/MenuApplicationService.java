package com.channel360.menu.application;

import com.channel360.common.dto.response.MenuItem;
import com.channel360.common.security.CustomUserDetails;
import com.channel360.menu.api.MenuRequest;
import com.channel360.menu.api.MenuResponse;

import com.channel360.menu.api.MenuUserProvider;
import com.channel360.menu.infrastructure.MenuItemRepository;
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

    private final MenuItemRepository menuItemRepository;
    private final MenuUserProvider menuUserProvider;

    public List<MenuItem> getCurrentUserMenu() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return List.of();
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<Long> roleIds = menuUserProvider.findRoleIdsByUserId(userDetails.getId());

        if (roleIds.isEmpty()) {
            return List.of();
        }

        Set<Long> visibleIds = new HashSet<>(menuItemRepository.findMenuItemIdsByRoleIds(roleIds));

        List<MenuItem> menuItems = new ArrayList<>();
        List<com.channel360.menu.domain.MenuItem> rootItems = menuItemRepository.findByParentIdIsNullAndActiveTrueOrderByDisplayOrder();

        for (com.channel360.menu.domain.MenuItem parent : rootItems) {
            if (visibleIds.contains(parent.getId()) || parent.getPermissionName() == null) {
                List<com.channel360.menu.domain.MenuItem> children = menuItemRepository.findByParentIdAndActiveTrueOrderByDisplayOrder(parent.getId())
                        .stream()
                        .filter(child -> visibleIds.contains(child.getId()))
                        .toList();

                if (parent.getPermissionName() == null && children.isEmpty()) {
                    continue;
                }

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
        return menuItemRepository.findAllByOrderByDisplayOrder().stream()
                .map(this::toMenuResponse)
                .toList();
    }

    @Transactional
    public MenuResponse createMenuItem(MenuRequest request) {
        com.channel360.menu.domain.MenuItem entity = new com.channel360.menu.domain.MenuItem();
        applyRequest(entity, request);
        com.channel360.menu.domain.MenuItem saved = menuItemRepository.save(entity);
        return toMenuResponse(saved);
    }

    @Transactional
    public MenuResponse updateMenuItem(Long id, MenuRequest request) {
        com.channel360.menu.domain.MenuItem entity = menuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Menu item not found with id: " + id));
        applyRequest(entity, request);
        com.channel360.menu.domain.MenuItem saved = menuItemRepository.save(entity);
        return toMenuResponse(saved);
    }

    @Transactional
    public void deleteMenuItem(Long id) {
        com.channel360.menu.domain.MenuItem entity = menuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Menu item not found with id: " + id));
        entity.setActive(false);
        menuItemRepository.save(entity);
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
        if (request.label() != null) entity.setLabel(request.label());
        if (request.path() != null) entity.setPath(request.path());
        if (request.icon() != null) entity.setIcon(request.icon());
        if (request.parentId() != null) entity.setParentId(request.parentId());
        if (request.permissionName() != null) entity.setPermissionName(request.permissionName());
        if (request.displayOrder() != null) entity.setDisplayOrder(request.displayOrder());
        if (request.active() != null) entity.setActive(request.active());
    }
}
