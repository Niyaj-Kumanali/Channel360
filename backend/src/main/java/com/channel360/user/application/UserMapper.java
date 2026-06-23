package com.channel360.user.application;

import com.channel360.user.api.CreateUserRequest;
import com.channel360.user.api.UpdateUserRequest;
import com.channel360.user.api.UserResponse;
import com.channel360.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {com.channel360.role.application.RoleMapper.class})
public interface UserMapper {
    UserResponse toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    User toEntity(CreateUserRequest request);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    void updateEntity(UpdateUserRequest request, @MappingTarget User user);
}
