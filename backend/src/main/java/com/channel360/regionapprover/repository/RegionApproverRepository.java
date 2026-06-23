package com.channel360.regionapprover.repository;

import com.channel360.regionapprover.entity.RegionApprover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionApproverRepository extends JpaRepository<RegionApprover, Long> {

    @Procedure("sp_region_approver_save")
    void spSave(
        @Param("p_id") Long id,
        @Param("p_region_id") Long regionId,
        @Param("p_role_id") Long roleId,
        @Param("p_user_id") Long userId,
        @Param("p_active_flag") Boolean activeFlag,
        @Param("p_user") String user
    );

    @Procedure("sp_region_approver_deactivate")
    void spDeactivate(@Param("p_id") Long id);

    List<RegionApprover> findByActiveFlagTrueOrderByRegionIdAsc();

    @Query("SELECT ra FROM RegionApprover ra WHERE ra.id = :id AND ra.activeFlag = true")
    Optional<RegionApprover> findActiveById(@Param("id") Long id);

    Optional<RegionApprover> findByRegionIdAndRoleIdAndUserIdAndActiveFlagTrue(Long regionId, Long roleId, Long userId);

    boolean existsByRegionIdAndActiveFlagTrue(Long regionId);
}
