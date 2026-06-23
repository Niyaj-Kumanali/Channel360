package com.channel360.menu.api;

import com.channel360.menu.domain.MenuItem;
import com.channel360.menu.infrastructure.MenuItemRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuFacadeImpl implements MenuFacade {

    private final MenuItemRepository menuItemRepository;

    @Override
    public List<Long> findMenuItemIdsByRoleIds(List<Long> roleIds) {
        return menuItemRepository.findMenuItemIdsByRoleIds(roleIds);
    }

    @Override
    public List<MenuItem> findRootMenuItems() {
        return menuItemRepository.findByParentIdIsNullAndActiveTrueOrderByDisplayOrder();
    }

    @Override
    public List<MenuItem> findChildMenuItems(Long parentId) {
        return menuItemRepository.findByParentIdAndActiveTrueOrderByDisplayOrder(parentId);
    }

    @Override
    public List<MenuItem> findAllOrdered() {
        return menuItemRepository.findAllByOrderByDisplayOrder();
    }

    @Override
    public MenuItem findById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));
    }

    @Override
    public MenuItem save(@NonNull MenuItem entity) {
        return menuItemRepository.save(entity);
    }

    @Override
    public boolean existsById(Long id) {
        return menuItemRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        menuItemRepository.deleteById(id);
    }
}
