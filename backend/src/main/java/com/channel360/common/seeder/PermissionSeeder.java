package com.channel360.common.seeder;

import com.channel360.role.api.request.CreatePermissionRequest;
import com.channel360.role.api.response.PermissionResponse;
import com.channel360.role.application.PermissionService;
import com.channel360.role.domain.Permission;
import com.channel360.role.infrastructure.PermissionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionSeeder {

    private final ObjectMapper objectMapper;
    private final PermissionRepository permissionRepository;

    public void seed() {
        try {
            String json = new String(
                    new ClassPathResource("db/seed/permissions.json").getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );
            List<Map<String, String>> permissions = objectMapper.readValue(json,
                    new TypeReference<List<Map<String, String>>>() {});

            for (Map<String, String> entry : permissions) {
                CreatePermissionRequest request = CreatePermissionRequest.builder()
                        .name(entry.get("name"))
                        .description(entry.get("description"))
                        .module(entry.get("module"))
                        .build();
                createIfNotExists(request);
                log.debug("Seeded permission: {}", entry.get("name"));
            }
        } catch (Exception e) {
            log.error("Failed to seed permissions", e);
        }
    }

    public void createIfNotExists(CreatePermissionRequest request) {
        Permission permission = permissionRepository.findByName(request.name())
                .orElseGet(() -> {
                    Permission p = new Permission();
                    p.setName(request.name());
                    p.setDescription(request.description());
                    p.setModule(request.module());
                    return permissionRepository.save(p);
                });
        PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .module(permission.getModule())
                .menuId(permission.getMenuId())
                .build();
    }
}
