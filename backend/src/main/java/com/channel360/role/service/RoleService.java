package com.channel360.role.service;

import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.role.dto.RoleDto;
import com.channel360.role.mapper.RoleMapper;
import com.channel360.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toDto)
                .toList();
    }

    public RoleDto getRoleById(Long id) {
        return roleRepository.findById(id)
                .map(roleMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
    }

    @Transactional
    public RoleDto createRole(RoleDto roleDto) {
        return roleMapper.toDto(
                roleRepository.create(roleDto.getName(), roleDto.getDescription())
        );
    }

    @Transactional
    public RoleDto updateRole(Long id, RoleDto roleDto) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role", "id", id);
        }
        roleRepository.update(id, roleDto.getName(), roleDto.getDescription());
        return roleRepository.findById(id)
                .map(roleMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
    }

    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role", "id", id);
        }
        roleRepository.deleteById(id);
    }
}
