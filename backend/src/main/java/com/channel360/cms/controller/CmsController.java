package com.channel360.cms.controller;

import com.channel360.cms.dto.*;
import com.channel360.cms.service.CmsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cms")
@RequiredArgsConstructor
public class CmsController {
    private final CmsService cmsService;

    @GetMapping("/sections/active")
    public ResponseEntity<List<HomepageSectionDto>> getActiveSections() {
        return ResponseEntity.ok(cmsService.getActiveSections());
    }

    @GetMapping("/sections")
    public ResponseEntity<List<HomepageSectionDto>> getAllSections() {
        return ResponseEntity.ok(cmsService.getAllSections());
    }

    @GetMapping("/sections/{id}")
    public ResponseEntity<HomepageSectionDto> getSectionById(@PathVariable Long id) {
        return ResponseEntity.ok(cmsService.getSectionById(id));
    }

    @PostMapping("/sections")
    public ResponseEntity<HomepageSectionDto> createSection(@Valid @RequestBody CreateSectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cmsService.createSection(request));
    }

    @PutMapping("/sections/{id}")
    public ResponseEntity<HomepageSectionDto> updateSection(@PathVariable Long id,
                                                            @Valid @RequestBody UpdateSectionRequest request) {
        return ResponseEntity.ok(cmsService.updateSection(id, request));
    }

    @DeleteMapping("/sections/{id}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id) {
        cmsService.deleteSection(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/sections/reorder")
    public ResponseEntity<Void> reorderSections(@Valid @RequestBody ReorderRequest request) {
        cmsService.reorderSections(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/sections/{id}/toggle")
    public ResponseEntity<HomepageSectionDto> toggleSectionActive(@PathVariable Long id) {
        return ResponseEntity.ok(cmsService.toggleSectionActive(id));
    }
}
