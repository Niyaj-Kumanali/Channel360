package com.channel360.popup.repository;

import com.channel360.popup.entity.HomepagePopup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HomepagePopupRepository extends JpaRepository<HomepagePopup, Long> {

    @Procedure("sp_homepage_popups_save")
    Long spSave(Long id, String title, String description, String imageUrl,
                String ctaButtonText, String ctaUrl, Integer priority,
                Boolean active, LocalDateTime startDate, LocalDateTime endDate,
                String createdBy, String modifiedBy);

    @Procedure("sp_homepage_popups_delete")
    void spDelete(Long id);

    @Procedure("sp_homepage_popups_get_active")
    List<HomepagePopup> spGetActive();

    @Procedure("sp_homepage_popups_list")
    List<HomepagePopup> spList();

    @Procedure("sp_homepage_popups_toggle_active")
    void spToggleActive(Long id, String modifiedBy);
}
