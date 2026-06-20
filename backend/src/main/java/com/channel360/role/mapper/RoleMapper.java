package com.channel360.role.mapper;

import com.channel360.role.dto.response.RoleResponse;
import com.channel360.role.entity.Permission;
import com.channel360.role.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "permissionNames")
    @Mapping(target = "permissionIds", source = "permissions", qualifiedByName = "permissionIds")
    RoleResponse toDto(Role role);

    @Mapping(target = "permissions", ignore = true)
    Role toEntity(RoleResponse roleDto);

    @Named("permissionNames")
    default List<String> mapPermissionNames(Set<Permission> permissions) {
        if (permissions == null) return Collections.emptyList();
        return permissions.stream().map(Permission::getName).toList();
    }

    @Named("permissionIds")
    default List<Long> mapPermissionIds(Set<Permission> permissions) {
        if (permissions == null) return Collections.emptyList();
        return permissions.stream().map(Permission::getId).toList();
    }
}
