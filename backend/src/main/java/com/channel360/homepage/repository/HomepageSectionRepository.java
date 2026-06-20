package com.channel360.homepage.repository;

import com.channel360.homepage.entity.HomepageSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HomepageSectionRepository extends JpaRepository<HomepageSection, Long> {

    @Procedure("sp_homepage_section_save")
    void spSave(
        @Param("p_id") Long id,
        @Param("p_section_name") String sectionName,
        @Param("p_section_type") String sectionType,
        @Param("p_title") String title,
        @Param("p_subtitle") String subtitle,
        @Param("p_description") String description,
        @Param("p_image_url") String imageUrl,
        @Param("p_button_text") String buttonText,
        @Param("p_button_url") String buttonUrl,
        @Param("p_display_order") Integer displayOrder,
        @Param("p_active") Boolean active,
        @Param("p_start_date") LocalDateTime startDate,
        @Param("p_end_date") LocalDateTime endDate,
        @Param("p_user") String user
    );

    @Procedure("sp_homepage_section_delete")
    void spDelete(@Param("p_id") Long id);

    @Query("SELECT s FROM HomepageSection s WHERE s.deletedFlag = false AND s.id = :id")
    Optional<HomepageSection> findActiveById(@Param("id") Long id);

    @Query("SELECT s FROM HomepageSection s WHERE s.deletedFlag = false ORDER BY s.displayOrder ASC, s.id DESC")
    List<HomepageSection> findAllActive();

    @Query("SELECT s FROM HomepageSection s WHERE s.deletedFlag = false AND s.active = true " +
           "AND (s.startDate IS NULL OR s.startDate <= CURRENT_TIMESTAMP) " +
           "AND (s.endDate IS NULL OR s.endDate >= CURRENT_TIMESTAMP) " +
           "ORDER BY s.displayOrder ASC, s.id DESC")
    List<HomepageSection> findAllPublished();
}
