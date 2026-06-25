package com.channel360.common.seeder;

import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.role.api.RoleResponse;
import com.channel360.role.application.RoleService;
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
public class RoleSeeder {

    private final RoleService roleService;
    private final ObjectMapper objectMapper;

    public void seed() {
        try {
            String json = new String(
                    new ClassPathResource("db/seed/roles.json").getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );
            List<Map<String, String>> roles = objectMapper.readValue(json,
                    new TypeReference<List<Map<String, String>>>() {});

            for (Map<String, String> entry : roles) {
                String name = entry.get("name");
                String description = entry.get("description");

                try {
                    roleService.getRoleByName(name);
                    log.debug("Role already exists: {}", name);
                } catch (ResourceNotFoundException e) {
                    RoleResponse dto = RoleResponse.builder()
                            .name(name)
                            .description(description)
                            .build();
                    roleService.createRole(dto);
                    log.info("Seeded role: {}", name);
                }
            }
        } catch (Exception e) {
            log.error("Failed to seed roles", e);
        }
    }
}
