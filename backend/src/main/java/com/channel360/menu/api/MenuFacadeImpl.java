package com.channel360.menu.api;

import com.channel360.common.dto.response.MenuItem;
import com.channel360.menu.application.MenuApplicationService;
import com.channel360.menu.infrastructure.MenuItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuFacadeImpl implements MenuFacade {

    private final MenuApplicationService menuApplicationService;
    private final MenuItemRepository menuItemRepository;

    @Override
    public List<Long> findMenuItemIdsByRoleIds(List<Long> roleIds) {
        return menuItemRepository.findMenuItemIdsByRoleIds(roleIds);
    }

    @Override
    public List<MenuItem> getCurrentUserMenu() {
        return menuApplicationService.getCurrentUserMenu();
    }

    @Override
    public List<MenuResponse> findAllOrdered() {
        return menuItemRepository.findAllByOrderByDisplayOrder().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public MenuResponse findById(Long id) {
        return menuItemRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Menu item not found with id: " + id));
    }

    @Override
    public boolean existsById(Long id) {
        return menuItemRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        menuItemRepository.deleteById(id);
    }

    private MenuResponse toResponse(com.channel360.menu.domain.MenuItem entity) {
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
}