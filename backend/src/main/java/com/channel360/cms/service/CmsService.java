package com.channel360.cms.service;

import com.channel360.cms.dto.*;
import com.channel360.cms.entity.HomepageSection;
import com.channel360.cms.mapper.HomepageSectionMapper;
import com.channel360.cms.repository.HomepageSectionRepository;
import com.channel360.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CmsService {
    private final HomepageSectionRepository repository;
    private final HomepageSectionMapper mapper;

    public List<HomepageSectionDto> getActiveSections() {
        return repository.getActive()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<HomepageSectionDto> getAllSections() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public HomepageSectionDto getSectionById(Long id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("HomepageSection", "id", id));
    }

    @Transactional
    public HomepageSectionDto createSection(CreateSectionRequest request) {
        HomepageSection section = mapper.toEntity(request);
        return mapper.toDto(
                repository.create(
                        section.getSectionName(), section.getSectionType(),
                        section.getTitle(), section.getSubtitle(),
                        section.getDescription(), section.getImageUrl(),
                        section.getButtonText(), section.getButtonUrl(),
                        section.getDisplayOrder(), section.isActive(),
                        section.getStartDate(), section.getEndDate(),
                        null
                )
        );
    }

    @Transactional
    public HomepageSectionDto updateSection(Long id, UpdateSectionRequest request) {
        HomepageSection section = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HomepageSection", "id", id));
        mapper.updateEntity(request, section);
        repository.update(
                id, section.getSectionName(), section.getSectionType(),
                section.getTitle(), section.getSubtitle(),
                section.getDescription(), section.getImageUrl(),
                section.getButtonText(), section.getButtonUrl(),
                section.getDisplayOrder(), section.isActive(),
                section.getStartDate(), section.getEndDate(),
                null
        );
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("HomepageSection", "id", id));
    }

    @Transactional
    public void deleteSection(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("HomepageSection", "id", id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public void reorderSections(ReorderRequest request) {
        String json = IntStream.range(0, request.getSectionIds().size())
                .mapToObj(i -> "{\"id\":" + request.getSectionIds().get(i) + ",\"displayOrder\":" + i + "}")
                .reduce((a, b) -> a + "," + b)
                .map(s -> "[" + s + "]")
                .orElse("[]");
        repository.reorderSections(json);
    }

    @Transactional
    public HomepageSectionDto toggleSectionActive(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("HomepageSection", "id", id);
        }
        repository.toggleActive(id, null);
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("HomepageSection", "id", id));
    }
}
