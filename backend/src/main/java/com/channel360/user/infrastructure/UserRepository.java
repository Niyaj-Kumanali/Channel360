package com.channel360.user.infrastructure;

import com.channel360.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    @Query("SELECT DISTINCT p.name FROM User u JOIN u.roles r JOIN r.permissions p WHERE u.id = :userId")
    Set<String> findPermissionNamesByUserId(@Param("userId") Long userId);

    @Query("SELECT r.id FROM User u JOIN u.roles r WHERE u.id = :userId")
    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT r.name FROM User u JOIN u.roles r WHERE u.id = :userId")
    Set<String> findRoleNamesByUserId(@Param("userId") Long userId);

    boolean existsByEmployeeId(String employeeId);

    Optional<User> findByEmployeeId(String employeeId);

    Optional<User> findTopByOrderByIdDesc();

    @Procedure("sp_users_save")
    void spSave(@Param("p_id") Long id, @Param("p_first_name") String firstName,
                @Param("p_last_name") String lastName, @Param("p_mobile_number") String mobileNumber,
                @Param("p_employee_id") String employeeId, @Param("p_status") String status,
                @Param("p_created_by") String createdBy, @Param("p_modified_by") String modifiedBy);

    @Procedure("sp_users_delete")
    void spDelete(@Param("p_id") Long id);

    @Procedure("sp_users_assign_roles")
    void spAssignRoles(@Param("p_user_id") Long userId,
                       @Param("p_role_ids") String roleIds,
                       @Param("p_modified_by") String modifiedBy);
}
