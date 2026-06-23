package com.channel360.menu.api;

import com.channel360.menu.domain.MenuItem;
import org.springframework.lang.NonNull;

import java.util.List;

public interface MenuFacade {
    List<Long> findMenuItemIdsByRoleIds(List<Long> roleIds);
    List<MenuItem> findRootMenuItems();
    List<MenuItem> findChildMenuItems(Long parentId);
    List<MenuItem> findAllOrdered();
    MenuItem findById(Long id);
    MenuItem save(@NonNull MenuItem entity);
    boolean existsById(Long id);
    void deleteById(Long id);
}
