package com.channel360.common.seeder;

import com.channel360.role.api.CreatePermissionRequest;
import com.channel360.role.application.PermissionService;
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

    private final PermissionService permissionService;
    private final ObjectMapper objectMapper;

    public void seed() {
        try {
            String json = new String(
                    new ClassPathResource("db/seed/permissions.json").getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );
            List<Map<String, String>> permissions = objectMapper.readValue(json,
                    new TypeReference<List<Map<String, String>>>() {});

            for (Map<String, String> entry : permissions) {
                CreatePermissionRequest request = new CreatePermissionRequest();
                request.setName(entry.get("name"));
                request.setDescription(entry.get("description"));
                request.setModule(entry.get("module"));
                permissionService.createIfNotExists(request);
                log.debug("Seeded permission: {}", entry.get("name"));
            }
        } catch (Exception e) {
            log.error("Failed to seed permissions", e);
        }
    }
}
