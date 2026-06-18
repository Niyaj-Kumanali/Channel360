package com.channel360.cms.entity;

import com.channel360.common.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "homepage_sections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HomepageSection extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String sectionName;

    @Column(nullable = false, length = 50)
    private String sectionType;

    @Column(length = 200)
    private String title;

    @Column(length = 300)
    private String subtitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 100)
    private String buttonText;

    @Column(length = 500)
    private String buttonUrl;

    private Integer displayOrder;

    @Column(nullable = false)
    private boolean active;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
