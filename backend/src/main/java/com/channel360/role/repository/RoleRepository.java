package com.channel360.role.repository;

import com.channel360.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    boolean existsByName(String name);

    @Procedure("sp_roles_save")
    Long spSave(Long id, String name, String description);

    @Procedure("sp_roles_delete")
    void spDelete(Long id);
}
