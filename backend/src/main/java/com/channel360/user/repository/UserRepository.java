package com.channel360.user.repository;

import com.channel360.common.response.PaginatedResult;
import com.channel360.role.entity.Role;
import com.channel360.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<User> findById(Long id) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_users_get_by_id", User.class)
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, void.class, ParameterMode.REF_CURSOR)
                .setParameter(1, id);
        query.execute();
        try {
            User user = (User) query.getSingleResult();
            user.setRoles(findRolesByUserId(id));
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByEmail(String email) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_users_get_by_email", User.class)
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, void.class, ParameterMode.REF_CURSOR)
                .setParameter(1, email);
        query.execute();
        try {
            User user = (User) query.getSingleResult();
            user.setRoles(findRolesByUserId(user.getId()));
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public boolean existsById(Long id) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_users_exists_by_id")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, Boolean.class, ParameterMode.OUT)
                .setParameter(1, id);
        query.execute();
        return Boolean.TRUE.equals(query.getOutputParameterValue(2));
    }

    public boolean existsByEmail(String email) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_users_exists_by_email")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, Boolean.class, ParameterMode.OUT)
                .setParameter(1, email);
        query.execute();
        return Boolean.TRUE.equals(query.getOutputParameterValue(2));
    }

    public boolean existsByEmployeeId(String employeeId) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_users_exists_by_employee_id")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, Boolean.class, ParameterMode.OUT)
                .setParameter(1, employeeId);
        query.execute();
        return Boolean.TRUE.equals(query.getOutputParameterValue(2));
    }

    public User create(String firstName, String lastName, String email, String password,
                       String mobileNumber, String employeeId, String status, String createdBy) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_users_create")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(5, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(6, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(7, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(8, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(9, Long.class, ParameterMode.INOUT)
                .setParameter(1, firstName)
                .setParameter(2, lastName)
                .setParameter(3, email)
                .setParameter(4, password)
                .setParameter(5, mobileNumber)
                .setParameter(6, employeeId)
                .setParameter(7, status)
                .setParameter(8, createdBy)
                .setParameter(9, null);
        query.execute();
        Long id = (Long) query.getOutputParameterValue(9);
        return findById(id).orElse(null);
    }

    public void update(Long id, String firstName, String lastName, String email,
                       String mobileNumber, String employeeId, String status, String modifiedBy) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_users_update")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(5, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(6, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(7, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(8, String.class, ParameterMode.IN)
                .setParameter(1, id)
                .setParameter(2, firstName)
                .setParameter(3, lastName)
                .setParameter(4, email)
                .setParameter(5, mobileNumber)
                .setParameter(6, employeeId)
                .setParameter(7, status)
                .setParameter(8, modifiedBy);
        query.execute();
    }

    public void deleteById(Long id) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_users_delete")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .setParameter(1, id);
        query.execute();
    }

    public void activate(Long id, String modifiedBy) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_users_activate")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .setParameter(1, id)
                .setParameter(2, modifiedBy);
        query.execute();
    }

    public void deactivate(Long id, String modifiedBy) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_users_deactivate")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .setParameter(1, id)
                .setParameter(2, modifiedBy);
        query.execute();
    }

    public void assignRoles(Long userId, List<Long> roleIds, String modifiedBy) {
        String joined = roleIds.stream()
                .map(String::valueOf)
                .collect(java.util.stream.Collectors.joining(","));
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_users_assign_roles")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .setParameter(1, userId)
                .setParameter(2, joined)
                .setParameter(3, modifiedBy);
        query.execute();
    }

    public void changePassword(Long id, String password) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_users_change_password")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .setParameter(1, id)
                .setParameter(2, password);
        query.execute();
    }

    public void updateLastLogin(Long id) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_users_update_last_login")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .setParameter(1, id);
        query.execute();
    }

    public Set<Role> findRolesByUserId(Long userId) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_users_get_roles", Role.class)
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, void.class, ParameterMode.REF_CURSOR)
                .setParameter(1, userId);
        query.execute();
        return new HashSet<>(query.getResultList());
    }

    @SuppressWarnings("unchecked")
    public PaginatedResult<User> listPaginated(String search, String status, Long roleId,
                                                int page, int size, String sortBy, String sortDir) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_users_list_paginated", User.class)
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter(5, Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter(6, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(7, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(8, void.class, ParameterMode.REF_CURSOR)
                .registerStoredProcedureParameter(9, Long.class, ParameterMode.OUT)
                .setParameter(1, search)
                .setParameter(2, status)
                .setParameter(3, roleId)
                .setParameter(4, page)
                .setParameter(5, size)
                .setParameter(6, sortBy)
                .setParameter(7, sortDir);
        query.execute();
        List<User> users = (List<User>) query.getResultList();
        long totalCount = (Long) query.getOutputParameterValue(9);
        return new PaginatedResult<>(users, totalCount);
    }
}
