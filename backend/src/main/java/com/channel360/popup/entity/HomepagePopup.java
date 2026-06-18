package com.channel360.popup.entity;

import com.channel360.common.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "homepage_popups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HomepagePopup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 100)
    private String ctaButtonText;

    @Column(length = 500)
    private String ctaUrl;

    private Integer priority;

    @Column(nullable = false)
    private boolean active;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
