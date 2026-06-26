package com.channel360.common.seeder;

import com.channel360.menu.api.MenuRequest;
import com.channel360.menu.application.MenuApplicationService;
import com.channel360.role.application.PermissionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class MenuSeeder {

    private final MenuApplicationService menuApplicationService;
    private final PermissionService permissionService;
    private final ObjectMapper objectMapper;

    public void seed() {
        try {
            String json = new String(
                    new ClassPathResource("db/seed/menus.json").getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );
            JsonNode root = objectMapper.readTree(json);

            for (JsonNode item : root) {
                String label = item.get("label").asText();
                if (menuExists(label, null)) {
                    log.debug("Menu item already exists: {}", label);
                    continue;
                }

                MenuRequest request = MenuRequest.builder()
                        .label(label)
                        .path(item.get("path").asText())
                        .icon(item.has("icon") && !item.get("icon").isNull() ? item.get("icon").asText() : null)
                        .displayOrder(item.get("displayOrder").asInt())
                        .permissionName(item.has("permissionName") && !item.get("permissionName").isNull()
                                ? item.get("permissionName").asText() : null)
                        .active(true)
                        .build();

                Long menuId = menuApplicationService.createMenuItem(request).id();

                if (request.permissionName() != null) {
                    permissionService.setMenuId(request.permissionName(), menuId);
                }

                JsonNode children = item.get("children");
                if (children != null && children.isArray()) {
                    for (JsonNode child : children) {
                        String childLabel = child.get("label").asText();
                        if (menuExists(childLabel, menuId)) {
                            log.debug("Menu child already exists: {}", childLabel);
                            continue;
                        }

                        MenuRequest childRequest = MenuRequest.builder()
                                .label(childLabel)
                                .path(child.get("path").asText())
                                .icon(child.has("icon") && !child.get("icon").isNull()
                                        ? child.get("icon").asText() : null)
                                .displayOrder(child.get("displayOrder").asInt())
                                .permissionName(child.has("permissionName") && !child.get("permissionName").isNull()
                                        ? child.get("permissionName").asText() : null)
                                .parentId(menuId)
                                .active(true)
                                .build();

                        Long childId = menuApplicationService.createMenuItem(childRequest).id();

                        if (childRequest.permissionName() != null) {
                            permissionService.setMenuId(childRequest.permissionName(), childId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to seed menu items", e);
        }
    }

    private boolean menuExists(String label, Long parentId) {
        return menuApplicationService.getAllMenuItems().stream()
                .anyMatch(m -> m.label().equals(label) && Objects.equals(m.parentId(), parentId));
    }
}
