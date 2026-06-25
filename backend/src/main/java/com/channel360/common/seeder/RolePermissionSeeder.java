package com.channel360.common.seeder;

import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.role.api.RoleResponse;
import com.channel360.role.application.RoleService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RolePermissionSeeder {

    private final RoleService roleService;
    private final ObjectMapper objectMapper;

    public void seed() {
        try {
            String json = new String(
                    new ClassPathResource("db/seed/role-permissions.json").getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );
            JsonNode root = objectMapper.readTree(json);
            Map<String, Long> permNameToId = roleService.getAllPermissions().stream()
                    .collect(Collectors.toMap(
                            p -> p.name().toLowerCase(),
                            p -> p.id()
                    ));

            for (JsonNode entry : root) {
                String roleName = entry.get("role").asText();
                JsonNode perms = entry.get("permissions");

                RoleResponse role;
                try {
                    role = roleService.getRoleByName(roleName);
                } catch (ResourceNotFoundException e) {
                    log.warn("Role not found: {}", roleName);
                    continue;
                }

                List<Long> permissionIds = new ArrayList<>();
                if (perms.isTextual() && "*".equals(perms.asText())) {
                    permissionIds.addAll(permNameToId.values());
                } else if (perms.isArray()) {
                    for (JsonNode p : perms) {
                        Long id = permNameToId.get(p.asText().toLowerCase());
                        if (id != null) {
                            permissionIds.add(id);
                        }
                    }
                }

                roleService.updateRolePermissions(role.getId(), permissionIds);
                log.info("Assigned {} permissions to role {}", permissionIds.size(), roleName);
            }
        } catch (Exception e) {
            log.error("Failed to seed role-permissions", e);
        }
    }
}
