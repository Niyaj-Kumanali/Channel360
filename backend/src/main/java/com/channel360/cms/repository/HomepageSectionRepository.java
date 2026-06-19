package com.channel360.cms.repository;

import com.channel360.cms.entity.HomepageSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HomepageSectionRepository extends JpaRepository<HomepageSection, Long> {

    @Procedure("sp_homepage_sections_save")
    Long spSave(Long id, String sectionName, String sectionType, String title,
                String subtitle, String description, String imageUrl,
                String buttonText, String buttonUrl, Integer displayOrder,
                Boolean active, LocalDateTime startDate, LocalDateTime endDate,
                String createdBy, String modifiedBy);

    @Procedure("sp_homepage_sections_delete")
    void spDelete(Long id);

    @Procedure("sp_homepage_sections_get_active")
    List<HomepageSection> spGetActive();

    @Procedure("sp_homepage_sections_list")
    List<HomepageSection> spList();

    @Procedure("sp_homepage_sections_reorder")
    void spReorder(String json);

    @Procedure("sp_homepage_sections_toggle_active")
    void spToggleActive(Long id, String modifiedBy);
}
