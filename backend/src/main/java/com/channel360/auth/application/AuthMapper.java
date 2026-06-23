package com.channel360.auth.application;

import com.channel360.auth.api.RegisterRequest;
import com.channel360.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    User registerRequestToUser(RegisterRequest request);
}
