package com.channel360.popup.service;

import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.popup.dto.*;
import com.channel360.popup.entity.HomepagePopup;
import com.channel360.popup.mapper.PopupMapper;
import com.channel360.popup.repository.HomepagePopupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopupService {
    private final HomepagePopupRepository repository;
    private final PopupMapper mapper;

    public List<HomepagePopupDto> getActivePopups() {
        return repository.spGetActive()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<HomepagePopupDto> getAllPopups() {
        return repository.spList()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public HomepagePopupDto getPopupById(Long id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("HomepagePopup", "id", id));
    }

    @Transactional
    public HomepagePopupDto createPopup(CreatePopupRequest request) {
        HomepagePopup popup = mapper.toEntity(request);
        Long id = repository.spSave(null, popup.getTitle(), popup.getDescription(),
                popup.getImageUrl(), popup.getCtaButtonText(), popup.getCtaUrl(),
                popup.getPriority(), popup.isActive(), popup.getStartDate(),
                popup.getEndDate(), null, null);
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("HomepagePopup", "id", id));
    }

    @Transactional
    public HomepagePopupDto updatePopup(Long id, UpdatePopupRequest request) {
        HomepagePopup popup = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HomepagePopup", "id", id));
        mapper.updateEntity(request, popup);
        repository.spSave(id, popup.getTitle(), popup.getDescription(),
                popup.getImageUrl(), popup.getCtaButtonText(), popup.getCtaUrl(),
                popup.getPriority(), popup.isActive(), popup.getStartDate(),
                popup.getEndDate(), null, null);
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("HomepagePopup", "id", id));
    }

    @Transactional
    public void deletePopup(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("HomepagePopup", "id", id);
        }
        repository.spDelete(id);
    }

    @Transactional
    public HomepagePopupDto togglePopupActive(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("HomepagePopup", "id", id);
        }
        repository.spToggleActive(id, null);
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("HomepagePopup", "id", id));
    }
}
