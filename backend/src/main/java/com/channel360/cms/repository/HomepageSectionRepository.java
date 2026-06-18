package com.channel360.cms.repository;

import com.channel360.cms.entity.HomepageSection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class HomepageSectionRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<HomepageSection> findById(Long id) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_homepage_sections_get_by_id", HomepageSection.class)
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, void.class, ParameterMode.REF_CURSOR)
                .setParameter(1, id);
        query.execute();
        try {
            return Optional.of((HomepageSection) query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public List<HomepageSection> getActive() {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_homepage_sections_get_active", HomepageSection.class)
                .registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        query.execute();
        return (List<HomepageSection>) query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<HomepageSection> findAll() {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_homepage_sections_get_all", HomepageSection.class)
                .registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        query.execute();
        return (List<HomepageSection>) query.getResultList();
    }

    public HomepageSection create(String sectionName, String sectionType, String title,
                                   String subtitle, String description, String imageUrl,
                                   String buttonText, String buttonUrl, Integer displayOrder,
                                   boolean active, java.time.LocalDateTime startDate,
                                   java.time.LocalDateTime endDate, String createdBy) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_homepage_sections_create")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(5, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(6, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(7, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(8, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(9, Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter(10, Boolean.class, ParameterMode.IN)
                .registerStoredProcedureParameter(11, java.time.LocalDateTime.class, ParameterMode.IN)
                .registerStoredProcedureParameter(12, java.time.LocalDateTime.class, ParameterMode.IN)
                .registerStoredProcedureParameter(13, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(14, Long.class, ParameterMode.INOUT)
                .setParameter(1, sectionName)
                .setParameter(2, sectionType)
                .setParameter(3, title)
                .setParameter(4, subtitle)
                .setParameter(5, description)
                .setParameter(6, imageUrl)
                .setParameter(7, buttonText)
                .setParameter(8, buttonUrl)
                .setParameter(9, displayOrder)
                .setParameter(10, active)
                .setParameter(11, startDate)
                .setParameter(12, endDate)
                .setParameter(13, createdBy)
                .setParameter(14, null);
        query.execute();
        Long id = (Long) query.getOutputParameterValue(14);
        return findById(id).orElse(null);
    }

    public void update(Long id, String sectionName, String sectionType, String title,
                       String subtitle, String description, String imageUrl,
                       String buttonText, String buttonUrl, Integer displayOrder,
                       Boolean active, java.time.LocalDateTime startDate,
                       java.time.LocalDateTime endDate, String modifiedBy) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_homepage_sections_update")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(5, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(6, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(7, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(8, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(9, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(10, Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter(11, Boolean.class, ParameterMode.IN)
                .registerStoredProcedureParameter(12, java.time.LocalDateTime.class, ParameterMode.IN)
                .registerStoredProcedureParameter(13, java.time.LocalDateTime.class, ParameterMode.IN)
                .registerStoredProcedureParameter(14, String.class, ParameterMode.IN)
                .setParameter(1, id)
                .setParameter(2, sectionName)
                .setParameter(3, sectionType)
                .setParameter(4, title)
                .setParameter(5, subtitle)
                .setParameter(6, description)
                .setParameter(7, imageUrl)
                .setParameter(8, buttonText)
                .setParameter(9, buttonUrl)
                .setParameter(10, displayOrder)
                .setParameter(11, active)
                .setParameter(12, startDate)
                .setParameter(13, endDate)
                .setParameter(14, modifiedBy);
        query.execute();
    }

    public void deleteById(Long id) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_homepage_sections_delete")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .setParameter(1, id);
        query.execute();
    }

    public void toggleActive(Long id, String modifiedBy) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_homepage_sections_toggle_active")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .setParameter(1, id)
                .setParameter(2, modifiedBy);
        query.execute();
    }

    public boolean existsById(Long id) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_homepage_sections_exists")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, Boolean.class, ParameterMode.OUT)
                .setParameter(1, id);
        query.execute();
        return Boolean.TRUE.equals(query.getOutputParameterValue(2));
    }

    public void reorderSections(String jsonData) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_homepage_sections_reorder")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .setParameter(1, jsonData);
        query.execute();
    }
}
