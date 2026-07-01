package com.channel360.homepage.api;

import com.channel360.common.dto.response.ApiResponse;
import com.channel360.common.security.RequirePermission;
import com.channel360.homepage.api.request.HomepagePopupRequest;
import com.channel360.homepage.api.request.HomepageSectionRequest;
import com.channel360.homepage.api.request.SectionReorderRequest;
import com.channel360.homepage.api.response.HomepagePopupResponse;
import com.channel360.homepage.api.response.HomepageSectionResponse;
import com.channel360.homepage.application.HomepageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/homepage")
@RequiredArgsConstructor
public class HomepageController {

    private final HomepageService homepageService;

    // --- Public endpoints ---

    @GetMapping("/sections")
    public ResponseEntity<ApiResponse<List<HomepageSectionResponse>>> getPublishedSections() {
        List<HomepageSectionResponse> sections = homepageService.getPublishedSections();
        return ResponseEntity.ok(ApiResponse.success(sections));
    }

    @GetMapping("/popups")
    public ResponseEntity<ApiResponse<List<HomepagePopupResponse>>> getActivePopups() {
        List<HomepagePopupResponse> popups = homepageService.getActivePopups();
        return ResponseEntity.ok(ApiResponse.success(popups));
    }

    // --- Admin: Sections ---

    @GetMapping("/sections/admin")
    @RequirePermission("sections.view")
    public ResponseEntity<ApiResponse<List<HomepageSectionResponse>>> getAllSections() {
        List<HomepageSectionResponse> sections = homepageService.getAllSections();
        return ResponseEntity.ok(ApiResponse.success(sections));
    }

    @GetMapping("/sections/{id}")
    @RequirePermission("sections.view")
    public ResponseEntity<ApiResponse<HomepageSectionResponse>> getSection(@PathVariable Long id, Authentication auth) {
        HomepageSectionResponse section = homepageService.getSection(id, auth.getName());
        if (section == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success(section));
    }

    @PostMapping("/sections")
    @RequirePermission("sections.create")
    public ResponseEntity<ApiResponse<HomepageSectionResponse>> createSection(
            @Valid @RequestBody HomepageSectionRequest request,
            Authentication auth) {
        HomepageSectionResponse section = homepageService.saveSection(request, auth.getName());
        return ResponseEntity.ok(ApiResponse.success(section, "Section created"));
    }

    @PutMapping("/sections/{id}")
    @RequirePermission("sections.edit")
    public ResponseEntity<ApiResponse<HomepageSectionResponse>> updateSection(
            @PathVariable Long id,
            @Valid @RequestBody HomepageSectionRequest request,
            Authentication auth) {
        HomepageSectionResponse section = homepageService.saveSection(
                HomepageSectionRequest.builder()
                        .id(id)
                        .sectionName(request.sectionName())
                        .sectionType(request.sectionType())
                        .title(request.title())
                        .subtitle(request.subtitle())
                        .description(request.description())
                        .imageUrl(request.imageUrl())
                        .buttonText(request.buttonText())
                        .buttonUrl(request.buttonUrl())
                        .displayOrder(request.displayOrder())
                        .active(request.active())
                        .startDate(request.startDate())
                        .endDate(request.endDate())
                        .build(),
                auth.getName());
        return ResponseEntity.ok(ApiResponse.success(section, "Section updated"));
    }

    @DeleteMapping("/sections/{id}")
    @RequirePermission("sections.delete")
    public ResponseEntity<ApiResponse<Void>> deleteSection(@PathVariable Long id) {
        homepageService.deleteSection(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Section deleted"));
    }

    @PutMapping("/sections/reorder")
    @RequirePermission("sections.edit")
    public ResponseEntity<ApiResponse<Void>> reorderSections(@Valid @RequestBody SectionReorderRequest request) {
        homepageService.reorderSections(request.items());
        return ResponseEntity.ok(ApiResponse.success(null, "Sections reordered"));
    }

    // --- Admin: Popups ---

    @GetMapping("/popups/admin")
    @RequirePermission("popups.view")
    public ResponseEntity<ApiResponse<List<HomepagePopupResponse>>> getAllPopups() {
        List<HomepagePopupResponse> popups = homepageService.getAllPopups();
        return ResponseEntity.ok(ApiResponse.success(popups));
    }

    @GetMapping("/popups/{id}")
    @RequirePermission("popups.view")
    public ResponseEntity<ApiResponse<HomepagePopupResponse>> getPopup(@PathVariable Long id) {
        HomepagePopupResponse popup = homepageService.getPopup(id);
        if (popup == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success(popup));
    }

    @PostMapping("/popups")
    @RequirePermission("popups.create")
    public ResponseEntity<ApiResponse<HomepagePopupResponse>> createPopup(
            @Valid @RequestBody HomepagePopupRequest request,
            Authentication auth) {
        HomepagePopupResponse popup = homepageService.savePopup(request, auth.getName());
        return ResponseEntity.ok(ApiResponse.success(popup, "Popup created"));
    }

    @PutMapping("/popups/{id}")
    @RequirePermission("popups.edit")
    public ResponseEntity<ApiResponse<HomepagePopupResponse>> updatePopup(
            @PathVariable Long id,
            @Valid @RequestBody HomepagePopupRequest request,
            Authentication auth) {
        HomepagePopupResponse popup = homepageService.savePopup(
                HomepagePopupRequest.builder()
                        .id(id)
                        .title(request.title())
                        .description(request.description())
                        .imageUrl(request.imageUrl())
                        .ctaButtonText(request.ctaButtonText())
                        .ctaUrl(request.ctaUrl())
                        .priority(request.priority())
                        .active(request.active())
                        .startDate(request.startDate())
                        .endDate(request.endDate())
                        .build(),
                auth.getName());
        return ResponseEntity.ok(ApiResponse.success(popup, "Popup updated"));
    }

    @DeleteMapping("/popups/{id}")
    @RequirePermission("popups.delete")
    public ResponseEntity<ApiResponse<Void>> deletePopup(@PathVariable Long id) {
        homepageService.deletePopup(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Popup deleted"));
    }
}
