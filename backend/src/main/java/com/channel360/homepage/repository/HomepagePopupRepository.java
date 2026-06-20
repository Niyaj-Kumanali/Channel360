package com.channel360.homepage.repository;

import com.channel360.homepage.entity.HomepagePopup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface HomepagePopupRepository extends JpaRepository<HomepagePopup, Long> {

    @Procedure("sp_homepage_popup_save")
    void spSave(
        @Param("p_id") Long id,
        @Param("p_title") String title,
        @Param("p_description") String description,
        @Param("p_image_url") String imageUrl,
        @Param("p_cta_button_text") String ctaButtonText,
        @Param("p_cta_url") String ctaUrl,
        @Param("p_priority") Integer priority,
        @Param("p_active") Boolean active,
        @Param("p_start_date") LocalDateTime startDate,
        @Param("p_end_date") LocalDateTime endDate,
        @Param("p_user") String user
    );

    @Procedure("sp_homepage_popup_delete")
    void spDelete(@Param("p_id") Long id);
}
