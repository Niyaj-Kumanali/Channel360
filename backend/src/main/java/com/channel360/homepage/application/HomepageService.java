package com.channel360.homepage.application;

import com.channel360.homepage.api.HomepagePopupRequest;
import com.channel360.homepage.api.HomepageSectionRequest;
import com.channel360.homepage.api.SectionReorderRequest;
import com.channel360.homepage.api.HomepagePopupResponse;
import com.channel360.homepage.api.HomepageSectionResponse;
import com.channel360.homepage.domain.HomepagePopup;
import com.channel360.homepage.domain.HomepageSection;
import com.channel360.homepage.infrastructure.HomepagePopupRepository;
import com.channel360.homepage.infrastructure.HomepageSectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomepageService {

    private final HomepageSectionRepository sectionRepository;
    private final HomepagePopupRepository popupRepository;

    @Transactional
    public HomepageSectionResponse saveSection(HomepageSectionRequest request, String user) {
        sectionRepository.spSave(
            request.id(),
            request.sectionName(),
            request.sectionType(),
            request.title(),
            request.subtitle(),
            request.description(),
            request.imageUrl(),
            request.buttonText(),
            request.buttonUrl(),
            request.displayOrder(),
            request.active() != null ? request.active() : true,
            request.startDate(),
            request.endDate(),
            user
        );
        if (request.id() == null) {
            List<HomepageSection> all = sectionRepository.findAllActive();
            return all.isEmpty() ? null : mapSection(all.get(0));
        }
        return sectionRepository.findActiveById(request.id())
                .map(this::mapSection)
                .orElse(null);
    }

    @Transactional
    public void deleteSection(Long id) {
        sectionRepository.spDelete(id);
    }

    public HomepageSectionResponse getSection(Long id, String user) {
        return sectionRepository.findActiveById(id)
                .map(this::mapSection)
                .orElse(null);
    }

    public List<HomepageSectionResponse> getAllSections() {
        return sectionRepository.findAllActive().stream()
                .map(this::mapSection)
                .toList();
    }

    public List<HomepageSectionResponse> getPublishedSections() {
        return sectionRepository.findAllPublished().stream()
                .map(this::mapSection)
                .toList();
    }

    @Transactional
    public void reorderSections(List<SectionReorderRequest.SectionOrderItem> items) {
        for (SectionReorderRequest.SectionOrderItem item : items) {
            sectionRepository.updateDisplayOrder(item.id(), item.displayOrder());
        }
    }

    @Transactional
    public HomepagePopupResponse savePopup(HomepagePopupRequest request, String user) {
        popupRepository.spSave(
            request.id(),
            request.title(),
            request.description(),
            request.imageUrl(),
            request.ctaButtonText(),
            request.ctaUrl(),
            request.priority(),
            request.active() != null ? request.active() : true,
            request.startDate(),
            request.endDate(),
            user
        );
        if (request.id() == null) {
            List<HomepagePopup> all = popupRepository.findAllActive();
            return all.isEmpty() ? null : mapPopup(all.get(0));
        }
        return popupRepository.findActiveById(request.id())
                .map(this::mapPopup)
                .orElse(null);
    }

    @Transactional
    public void deletePopup(Long id) {
        popupRepository.spDelete(id);
    }

    public HomepagePopupResponse getPopup(Long id) {
        return popupRepository.findActiveById(id)
                .map(this::mapPopup)
                .orElse(null);
    }

    public List<HomepagePopupResponse> getAllPopups() {
        return popupRepository.findAllActive().stream()
                .map(this::mapPopup)
                .toList();
    }

    public List<HomepagePopupResponse> getActivePopups() {
        return popupRepository.findAllPublished().stream()
                .map(this::mapPopup)
                .toList();
    }

    private HomepageSectionResponse mapSection(HomepageSection s) {
        return HomepageSectionResponse.builder()
            .id(s.getId())
            .sectionName(s.getSectionName())
            .sectionType(s.getSectionType())
            .title(s.getTitle())
            .subtitle(s.getSubtitle())
            .description(s.getDescription())
            .imageUrl(s.getImageUrl())
            .buttonText(s.getButtonText())
            .buttonUrl(s.getButtonUrl())
            .displayOrder(s.getDisplayOrder())
            .active(s.getActive())
            .startDate(s.getStartDate())
            .endDate(s.getEndDate())
            .createdBy(s.getCreatedBy())
            .createdAt(s.getCreatedAt())
            .updatedBy(s.getUpdatedBy())
            .updatedAt(s.getUpdatedAt())
            .build();
    }

    private HomepagePopupResponse mapPopup(HomepagePopup p) {
        return HomepagePopupResponse.builder()
            .id(p.getId())
            .title(p.getTitle())
            .description(p.getDescription())
            .imageUrl(p.getImageUrl())
            .ctaButtonText(p.getCtaButtonText())
            .ctaUrl(p.getCtaUrl())
            .priority(p.getPriority())
            .active(p.getActive())
            .startDate(p.getStartDate())
            .endDate(p.getEndDate())
            .createdBy(p.getCreatedBy())
            .createdAt(p.getCreatedAt())
            .updatedBy(p.getUpdatedBy())
            .updatedAt(p.getUpdatedAt())
            .build();
    }
}
