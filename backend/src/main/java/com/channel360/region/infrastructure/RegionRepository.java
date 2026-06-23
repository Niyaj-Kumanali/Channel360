package com.channel360.region.infrastructure;

import com.channel360.region.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    @Procedure("sp_regions_save")
    void spSave(
        @Param("p_id") Long id,
        @Param("p_name") String name,
        @Param("p_parent_id") Long parentId,
        @Param("p_level") String level,
        @Param("p_tree_type") String treeType,
        @Param("p_user") String user
    );

    @Procedure("sp_regions_delete")
    void spDelete(@Param("p_id") Long id);

    List<Region> findByDeletedFlagFalseAndTreeTypeOrderByPath(String treeType);

    List<Region> findByDeletedFlagFalseOrderByPath();

    @Query("SELECT r FROM Region r WHERE r.deletedFlag = false AND r.id = :id")
    Optional<Region> findActiveById(@Param("id") Long id);

    @Query("SELECT r FROM Region r WHERE r.deletedFlag = false AND r.name = :name")
    Optional<Region> findActiveByName(@Param("name") String name);
}
