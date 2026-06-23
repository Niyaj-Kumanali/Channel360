package com.channel360.menu.api;

import com.channel360.menu.domain.MenuItem;
import com.channel360.menu.infrastructure.MenuItemRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuFacade {

    private final MenuItemRepository menuItemRepository;

    public List<Long> findMenuItemIdsByRoleIds(List<Long> roleIds) {
        return menuItemRepository.findMenuItemIdsByRoleIds(roleIds);
    }

    public List<MenuItem> findRootMenuItems() {
        return menuItemRepository.findByParentIdIsNullAndActiveTrueOrderByDisplayOrder();
    }

    public List<MenuItem> findChildMenuItems(Long parentId) {
        return menuItemRepository.findByParentIdAndActiveTrueOrderByDisplayOrder(parentId);
    }

    public List<MenuItem> findAllOrdered() {
        return menuItemRepository.findAllByOrderByDisplayOrder();
    }

    public MenuItem findById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));
    }

    public MenuItem save(@NonNull MenuItem entity) {
        return menuItemRepository.save(entity);
    }

    public boolean existsById(Long id) {
        return menuItemRepository.existsById(id);
    }

    public void deleteById(Long id) {
        menuItemRepository.deleteById(id);
    }
}
