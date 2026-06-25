package com.channel360.menu.api;

import com.channel360.common.dto.response.MenuItem;

import java.util.List;

public interface MenuFacade {
    List<Long> findMenuItemIdsByRoleIds(List<Long> roleIds);
    List<MenuItem> getCurrentUserMenu();
    List<MenuResponse> findAllOrdered();
    MenuResponse findById(Long id);
    boolean existsById(Long id);
    void deleteById(Long id);
}
