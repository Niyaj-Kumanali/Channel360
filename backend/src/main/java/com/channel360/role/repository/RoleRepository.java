package com.channel360.role.repository;

import com.channel360.role.entity.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoleRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<Role> findById(Long id) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_roles_get_by_id", Role.class)
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, void.class, ParameterMode.REF_CURSOR)
                .setParameter(1, id);
        query.execute();
        try {
            return Optional.of((Role) query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Role> findByName(String name) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_roles_find_by_name", Role.class)
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, void.class, ParameterMode.REF_CURSOR)
                .setParameter(1, name);
        query.execute();
        try {
            return Optional.of((Role) query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Role> findAll() {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_roles_get_all", Role.class)
                .registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        query.execute();
        return (List<Role>) query.getResultList();
    }

    public Role create(String name, String description) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_roles_create")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, Long.class, ParameterMode.INOUT)
                .setParameter(1, name)
                .setParameter(2, description)
                .setParameter(3, null);
        query.execute();
        Long id = (Long) query.getOutputParameterValue(3);
        return findById(id).orElse(null);
    }

    public void update(Long id, String name, String description) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_roles_update")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .setParameter(1, id)
                .setParameter(2, name)
                .setParameter(3, description);
        query.execute();
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    public boolean existsByName(String name) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_roles_exists_by_name")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, Boolean.class, ParameterMode.OUT)
                .setParameter(1, name);
        query.execute();
        return Boolean.TRUE.equals(query.getOutputParameterValue(2));
    }

    public void deleteById(Long id) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_roles_delete")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .setParameter(1, id);
        query.execute();
    }
}
