package com.channel360.homepage.service;

import com.channel360.homepage.dto.request.HomepagePopupRequest;
import com.channel360.homepage.dto.request.HomepageSectionRequest;
import com.channel360.homepage.dto.response.HomepagePopupResponse;
import com.channel360.homepage.dto.response.HomepageSectionResponse;
import com.channel360.homepage.entity.HomepagePopup;
import com.channel360.homepage.entity.HomepageSection;
import com.channel360.homepage.repository.HomepagePopupRepository;
import com.channel360.homepage.repository.HomepageSectionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomepageService {

    private final HomepageSectionRepository sectionRepository;
    private final HomepagePopupRepository popupRepository;
    private final EntityManager entityManager;

    @Transactional
    public HomepageSectionResponse saveSection(HomepageSectionRequest request, String user) {
        sectionRepository.spSave(
            request.getId(),
            request.getSectionName(),
            request.getSectionType(),
            request.getTitle(),
            request.getSubtitle(),
            request.getDescription(),
            request.getImageUrl(),
            request.getButtonText(),
            request.getButtonUrl(),
            request.getDisplayOrder(),
            request.getActive() != null ? request.getActive() : true,
            request.getStartDate(),
            request.getEndDate(),
            user
        );
        if (request.getId() == null) {
            return getSection(findLastInsertedId("homepage_sections"), user);
        }
        return getSection(request.getId(), user);
    }

    @Transactional
    public void deleteSection(Long id) {
        sectionRepository.spDelete(id);
    }

    public HomepageSectionResponse getSection(Long id, String user) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_homepage_section_get");
        query.registerStoredProcedureParameter("p_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_data", void.class, ParameterMode.REF_CURSOR);
        query.setParameter("p_id", id);
        query.execute();
        List<Object[]> results = query.getResultList();
        if (results.isEmpty()) return null;
        return mapSection(results.get(0));
    }

    public List<HomepageSectionResponse> getAllSections() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_homepage_sections_list");
        query.registerStoredProcedureParameter("p_data", void.class, ParameterMode.REF_CURSOR);
        query.execute();
        List<Object[]> results = query.getResultList();
        return results.stream().map(this::mapSection).toList();
    }

    public List<HomepageSectionResponse> getPublishedSections() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_homepage_sections_published");
        query.registerStoredProcedureParameter("p_data", void.class, ParameterMode.REF_CURSOR);
        query.execute();
        List<Object[]> results = query.getResultList();
        return results.stream().map(this::mapSection).toList();
    }

    @Transactional
    public HomepagePopupResponse savePopup(HomepagePopupRequest request, String user) {
        popupRepository.spSave(
            request.getId(),
            request.getTitle(),
            request.getDescription(),
            request.getImageUrl(),
            request.getCtaButtonText(),
            request.getCtaUrl(),
            request.getPriority(),
            request.getActive() != null ? request.getActive() : true,
            request.getStartDate(),
            request.getEndDate(),
            user
        );
        if (request.getId() == null) {
            return getPopup(findLastInsertedId("homepage_popups"));
        }
        return getPopup(request.getId());
    }

    @Transactional
    public void deletePopup(Long id) {
        popupRepository.spDelete(id);
    }

    public HomepagePopupResponse getPopup(Long id) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_homepage_popup_get");
        query.registerStoredProcedureParameter("p_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_data", void.class, ParameterMode.REF_CURSOR);
        query.setParameter("p_id", id);
        query.execute();
        List<Object[]> results = query.getResultList();
        if (results.isEmpty()) return null;
        return mapPopup(results.get(0));
    }

    public List<HomepagePopupResponse> getAllPopups() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_homepage_popups_list");
        query.registerStoredProcedureParameter("p_data", void.class, ParameterMode.REF_CURSOR);
        query.execute();
        List<Object[]> results = query.getResultList();
        return results.stream().map(this::mapPopup).toList();
    }

    public List<HomepagePopupResponse> getActivePopups() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_homepage_popups_active");
        query.registerStoredProcedureParameter("p_data", void.class, ParameterMode.REF_CURSOR);
        query.execute();
        List<Object[]> results = query.getResultList();
        return results.stream().map(this::mapPopup).toList();
    }

    private Long findLastInsertedId(String tableName) {
        var query = entityManager.createNativeQuery("SELECT MAX(id) FROM " + tableName);
        Number result = (Number) query.getSingleResult();
        return result != null ? result.longValue() : null;
    }

    private HomepageSectionResponse mapSection(Object[] row) {
        return HomepageSectionResponse.builder()
            .id(toLong(row[0]))
            .sectionName((String) row[1])
            .sectionType((String) row[2])
            .title((String) row[3])
            .subtitle((String) row[4])
            .description((String) row[5])
            .imageUrl((String) row[6])
            .buttonText((String) row[7])
            .buttonUrl((String) row[8])
            .displayOrder(toInt(row[9]))
            .active(toBool(row[10]))
            .startDate(toLocalDateTime(row[11]))
            .endDate(toLocalDateTime(row[12]))
            .createdBy((String) row[13])
            .createdAt(toLocalDateTime(row[14]))
            .updatedBy((String) row[15])
            .updatedAt(toLocalDateTime(row[16]))
            .build();
    }

    private HomepagePopupResponse mapPopup(Object[] row) {
        return HomepagePopupResponse.builder()
            .id(toLong(row[0]))
            .title((String) row[1])
            .description((String) row[2])
            .imageUrl((String) row[3])
            .ctaButtonText((String) row[4])
            .ctaUrl((String) row[5])
            .priority(toInt(row[6]))
            .active(toBool(row[7]))
            .startDate(toLocalDateTime(row[8]))
            .endDate(toLocalDateTime(row[9]))
            .createdBy((String) row[10])
            .createdAt(toLocalDateTime(row[11]))
            .updatedBy((String) row[12])
            .updatedAt(toLocalDateTime(row[13]))
            .build();
    }

    private Long toLong(Object val) {
        return val instanceof Number ? ((Number) val).longValue() : null;
    }

    private Integer toInt(Object val) {
        return val instanceof Number ? ((Number) val).intValue() : null;
    }

    private Boolean toBool(Object val) {
        if (val instanceof Boolean) return (Boolean) val;
        return val != null;
    }

    private java.time.LocalDateTime toLocalDateTime(Object val) {
        if (val instanceof java.time.LocalDateTime) return (java.time.LocalDateTime) val;
        if (val instanceof java.sql.Timestamp) return ((java.sql.Timestamp) val).toLocalDateTime();
        return null;
    }
}
