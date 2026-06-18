package com.channel360.popup.controller;

import com.channel360.popup.dto.*;
import com.channel360.popup.service.PopupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/popups")
@RequiredArgsConstructor
public class PopupController {
    private final PopupService popupService;

    @GetMapping("/active")
    public ResponseEntity<List<HomepagePopupDto>> getActivePopups() {
        return ResponseEntity.ok(popupService.getActivePopups());
    }

    @GetMapping
    public ResponseEntity<List<HomepagePopupDto>> getAllPopups() {
        return ResponseEntity.ok(popupService.getAllPopups());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HomepagePopupDto> getPopupById(@PathVariable Long id) {
        return ResponseEntity.ok(popupService.getPopupById(id));
    }

    @PostMapping
    public ResponseEntity<HomepagePopupDto> createPopup(@Valid @RequestBody CreatePopupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(popupService.createPopup(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HomepagePopupDto> updatePopup(@PathVariable Long id,
                                                        @Valid @RequestBody UpdatePopupRequest request) {
        return ResponseEntity.ok(popupService.updatePopup(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePopup(@PathVariable Long id) {
        popupService.deletePopup(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<HomepagePopupDto> togglePopupActive(@PathVariable Long id) {
        return ResponseEntity.ok(popupService.togglePopupActive(id));
    }
}
