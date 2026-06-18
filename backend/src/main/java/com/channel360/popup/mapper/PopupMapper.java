package com.channel360.popup.mapper;

import com.channel360.popup.dto.CreatePopupRequest;
import com.channel360.popup.dto.HomepagePopupDto;
import com.channel360.popup.dto.UpdatePopupRequest;
import com.channel360.popup.entity.HomepagePopup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PopupMapper {
    HomepagePopupDto toDto(HomepagePopup popup);

    @Mapping(target = "active", constant = "true")
    HomepagePopup toEntity(CreatePopupRequest request);

    void updateEntity(UpdatePopupRequest request, @MappingTarget HomepagePopup popup);
}
