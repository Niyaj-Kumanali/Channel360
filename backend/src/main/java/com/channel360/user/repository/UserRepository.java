package com.channel360.user.repository;

import com.channel360.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmployeeId(String employeeId);

    @Procedure("sp_users_save")
    Long spSave(Long id, String firstName, String lastName, String email,
                String password, String mobileNumber, String employeeId,
                String status, String createdBy, String modifiedBy);

    @Procedure("sp_users_delete")
    void spDelete(Long id);

    @Procedure("sp_users_assign_roles")
    void spAssignRoles(Long userId, String roleIds, String modifiedBy);

    @Procedure("sp_users_change_password")
    void spChangePassword(Long id, String password);

    @Procedure("sp_users_list")
    List<User> spList(String search, String status, Long roleId,
                      Integer page, Integer size, String sortBy, String sortDir);

    @Procedure("sp_users_count")
    Long spCount(String search, String status, Long roleId);
}
