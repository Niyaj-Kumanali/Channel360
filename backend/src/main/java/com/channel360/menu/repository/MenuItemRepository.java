package com.channel360.menu.repository;

import com.channel360.menu.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByParentIdIsNullAndActiveTrueOrderByDisplayOrder();

    List<MenuItem> findByParentIdAndActiveTrueOrderByDisplayOrder(Long parentId);

    List<MenuItem> findAllByOrderByDisplayOrder();

    @Query(value = "SELECT menu_item_id FROM menu_item_roles WHERE role_id IN :roleIds", nativeQuery = true)
    List<Long> findMenuItemIdsByRoleIds(@Param("roleIds") List<Long> roleIds);

    @Query(value = "SELECT menu_item_id FROM menu_item_roles WHERE role_id = :roleId", nativeQuery = true)
    List<Long> findMenuItemIdsByRoleId(@Param("roleId") Long roleId);

    @Query(value = "SELECT role_id FROM menu_item_roles WHERE menu_item_id = :menuItemId", nativeQuery = true)
    List<Long> findRoleIdsByMenuItemId(@Param("menuItemId") Long menuItemId);

    @Modifying
    @Query(value = "DELETE FROM menu_item_roles WHERE role_id = :roleId", nativeQuery = true)
    void deleteRoleMenuItems(@Param("roleId") Long roleId);

    @Modifying
    @Query(value = "INSERT INTO menu_item_roles (menu_item_id, role_id) VALUES (:menuItemId, :roleId)", nativeQuery = true)
    void addRoleMenuItem(@Param("menuItemId") Long menuItemId, @Param("roleId") Long roleId);

    @Query(value = "SELECT permission_name FROM menu_items WHERE id IN :ids AND permission_name IS NOT NULL", nativeQuery = true)
    List<String> findPermissionNamesByMenuItemIds(@Param("ids") List<Long> ids);

    @Query(value = "SELECT COUNT(*) FROM menu_items mi JOIN menu_item_roles mir ON mir.menu_item_id = mi.id WHERE mir.role_id = :roleId AND mi.permission_name = :permissionName", nativeQuery = true)
    Long countAssignedMenuItemsWithPermission(@Param("roleId") Long roleId, @Param("permissionName") String permissionName);

    @Modifying
    @Query(value = "INSERT INTO role_permissions (role_id, permission_id) SELECT :roleId, p.id FROM permissions p WHERE p.name = :permissionName AND NOT EXISTS (SELECT 1 FROM role_permissions rp WHERE rp.role_id = :roleId AND rp.permission_id = p.id)", nativeQuery = true)
    void addRolePermissionIfNotExists(@Param("roleId") Long roleId, @Param("permissionName") String permissionName);

    @Modifying
    @Query(value = "DELETE FROM role_permissions rp WHERE rp.role_id = :roleId AND rp.permission_id = (SELECT p.id FROM permissions p WHERE p.name = :permissionName)", nativeQuery = true)
    void deleteRolePermissionByName(@Param("roleId") Long roleId, @Param("permissionName") String permissionName);
}
