package com.channel360.auth.repository;

import com.channel360.auth.entity.RefreshToken;
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
public class RefreshTokenRepository {

    @PersistenceContext
    private EntityManager em;

    public RefreshToken create(String token, Long userId, LocalDateTime expiryDate) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_refresh_tokens_create")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, LocalDateTime.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, Long.class, ParameterMode.INOUT)
                .setParameter(1, userId)
                .setParameter(2, token)
                .setParameter(3, expiryDate)
                .setParameter(4, null);
        query.execute();
        Long id = (Long) query.getOutputParameterValue(4);
        return findByToken(token).orElse(null);
    }

    public Optional<RefreshToken> findByToken(String token) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_refresh_tokens_find_by_token", RefreshToken.class)
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, void.class, ParameterMode.REF_CURSOR)
                .setParameter(1, token);
        query.execute();
        try {
            return Optional.of((RefreshToken) query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<RefreshToken> findByUserId(Long userId) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_refresh_tokens_find_by_user_id", RefreshToken.class)
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, void.class, ParameterMode.REF_CURSOR)
                .setParameter(1, userId);
        query.execute();
        return (List<RefreshToken>) query.getResultList();
    }

    public void revoke(String token) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_refresh_tokens_revoke")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .setParameter(1, token);
        query.execute();
    }

    public void deleteByUserId(Long userId) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_refresh_tokens_delete_by_user_id")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .setParameter(1, userId);
        query.execute();
    }
}
