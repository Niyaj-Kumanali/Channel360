package com.channel360.menu.infrastructure;

import com.channel360.menu.domain.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByParentIdIsNullAndActiveTrueOrderByDisplayOrder();

    List<MenuItem> findByParentIdAndActiveTrueOrderByDisplayOrder(Long parentId);

    List<MenuItem> findAllByOrderByDisplayOrder();

    @Query(value = "SELECT DISTINCT p.menu_id FROM permissions p "
            + "JOIN role_permissions rp ON rp.permission_id = p.id "
            + "WHERE rp.role_id IN (:roleIds) AND p.menu_id IS NOT NULL", nativeQuery = true)
    List<Long> findMenuItemIdsByRoleIds(@Param("roleIds") List<Long> roleIds);
}
