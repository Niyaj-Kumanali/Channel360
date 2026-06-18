package com.channel360.popup.repository;

import com.channel360.popup.entity.HomepagePopup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class HomepagePopupRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<HomepagePopup> findById(Long id) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_homepage_popups_get_by_id", HomepagePopup.class)
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, void.class, ParameterMode.REF_CURSOR)
                .setParameter(1, id);
        query.execute();
        try {
            return Optional.of((HomepagePopup) query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public List<HomepagePopup> getActive() {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_homepage_popups_get_active", HomepagePopup.class)
                .registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        query.execute();
        return (List<HomepagePopup>) query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<HomepagePopup> findAll() {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_homepage_popups_get_all", HomepagePopup.class)
                .registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        query.execute();
        return (List<HomepagePopup>) query.getResultList();
    }

    public HomepagePopup create(String title, String description, String imageUrl,
                                 String ctaButtonText, String ctaUrl, Integer priority,
                                 boolean active, LocalDateTime startDate,
                                 LocalDateTime endDate, String createdBy) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_homepage_popups_create")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(5, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(6, Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter(7, Boolean.class, ParameterMode.IN)
                .registerStoredProcedureParameter(8, LocalDateTime.class, ParameterMode.IN)
                .registerStoredProcedureParameter(9, LocalDateTime.class, ParameterMode.IN)
                .registerStoredProcedureParameter(10, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(11, Long.class, ParameterMode.INOUT)
                .setParameter(1, title)
                .setParameter(2, description)
                .setParameter(3, imageUrl)
                .setParameter(4, ctaButtonText)
                .setParameter(5, ctaUrl)
                .setParameter(6, priority)
                .setParameter(7, active)
                .setParameter(8, startDate)
                .setParameter(9, endDate)
                .setParameter(10, createdBy)
                .setParameter(11, null);
        query.execute();
        Long id = (Long) query.getOutputParameterValue(11);
        return findById(id).orElse(null);
    }

    public void update(Long id, String title, String description, String imageUrl,
                       String ctaButtonText, String ctaUrl, Integer priority,
                       Boolean active, LocalDateTime startDate,
                       LocalDateTime endDate, String modifiedBy) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_homepage_popups_update")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(5, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(6, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(7, Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter(8, Boolean.class, ParameterMode.IN)
                .registerStoredProcedureParameter(9, LocalDateTime.class, ParameterMode.IN)
                .registerStoredProcedureParameter(10, LocalDateTime.class, ParameterMode.IN)
                .registerStoredProcedureParameter(11, String.class, ParameterMode.IN)
                .setParameter(1, id)
                .setParameter(2, title)
                .setParameter(3, description)
                .setParameter(4, imageUrl)
                .setParameter(5, ctaButtonText)
                .setParameter(6, ctaUrl)
                .setParameter(7, priority)
                .setParameter(8, active)
                .setParameter(9, startDate)
                .setParameter(10, endDate)
                .setParameter(11, modifiedBy);
        query.execute();
    }

    public void deleteById(Long id) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_homepage_popups_delete")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .setParameter(1, id);
        query.execute();
    }

    public void toggleActive(Long id, String modifiedBy) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_homepage_popups_toggle_active")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .setParameter(1, id)
                .setParameter(2, modifiedBy);
        query.execute();
    }

    public boolean existsById(Long id) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_homepage_popups_exists")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, Boolean.class, ParameterMode.OUT)
                .setParameter(1, id);
        query.execute();
        return Boolean.TRUE.equals(query.getOutputParameterValue(2));
    }
}
