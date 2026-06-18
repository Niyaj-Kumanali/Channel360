package com.channel360.cms.mapper;

import com.channel360.cms.dto.CreateSectionRequest;
import com.channel360.cms.dto.HomepageSectionDto;
import com.channel360.cms.dto.UpdateSectionRequest;
import com.channel360.cms.entity.HomepageSection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HomepageSectionMapper {
    HomepageSectionDto toDto(HomepageSection section);

    @Mapping(target = "active", constant = "true")
    HomepageSection toEntity(CreateSectionRequest request);

    void updateEntity(UpdateSectionRequest request, @MappingTarget HomepageSection section);
}
