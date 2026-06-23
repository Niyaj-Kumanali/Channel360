package com.channel360.role.infrastructure;

import com.channel360.role.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    boolean existsByName(String name);

    @Procedure("sp_roles_save")
    void spSave(@Param("p_id") Long id, @Param("p_name") String name,
                @Param("p_description") String description);

    @Procedure("sp_roles_delete")
    void spDelete(@Param("p_id") Long id);
}
