package com.channel360.role.mapper;

import com.channel360.role.dto.response.RoleResponse;
import com.channel360.role.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {
    RoleResponse toDto(Role role);
    Role toEntity(RoleResponse roleDto);
}
