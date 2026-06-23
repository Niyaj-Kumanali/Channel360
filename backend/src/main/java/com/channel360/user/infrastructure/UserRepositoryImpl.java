package com.channel360.user.infrastructure;

import com.channel360.user.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> spList(String search, String status, Long roleId,
                             Integer page, Integer size, String sortBy, String sortDir) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_users_list", User.class);
        query.registerStoredProcedureParameter("p_search", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_status", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_role_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_page", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_size", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_sort_by", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_sort_dir", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_data", void.class, ParameterMode.REF_CURSOR);

        query.setParameter("p_search", search);
        query.setParameter("p_status", status);
        query.setParameter("p_role_id", roleId);
        query.setParameter("p_page", page);
        query.setParameter("p_size", size);
        query.setParameter("p_sort_by", sortBy);
        query.setParameter("p_sort_dir", sortDir);

        query.execute();
        return query.getResultList();
    }

    @Override
    public Long spCount(String search, String status, Long roleId) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_users_count");
        query.registerStoredProcedureParameter("p_search", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_status", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_role_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_total", Long.class, ParameterMode.OUT);

        query.setParameter("p_search", search);
        query.setParameter("p_status", status);
        query.setParameter("p_role_id", roleId);

        query.execute();
        return (Long) query.getOutputParameterValue("p_total");
    }
}
