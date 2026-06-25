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

                MenuRequest request = new MenuRequest();
                request.setLabel(label);
                request.setPath(item.get("path").asText());
                request.setIcon(item.has("icon") && !item.get("icon").isNull() ? item.get("icon").asText() : null);
                request.setDisplayOrder(item.get("displayOrder").asInt());
                request.setPermissionName(item.has("permissionName") && !item.get("permissionName").isNull()
                        ? item.get("permissionName").asText() : null);
                request.setActive(true);

                Long menuId = menuApplicationService.createMenuItem(request).id();

                if (request.getPermissionName() != null) {
                    permissionService.setMenuId(request.getPermissionName(), menuId);
                }

                JsonNode children = item.get("children");
                if (children != null && children.isArray()) {
                    for (JsonNode child : children) {
                        String childLabel = child.get("label").asText();
                        if (menuExists(childLabel, menuId)) {
                            log.debug("Menu child already exists: {}", childLabel);
                            continue;
                        }

                        MenuRequest childRequest = new MenuRequest();
                        childRequest.setLabel(childLabel);
                        childRequest.setPath(child.get("path").asText());
                        childRequest.setIcon(child.has("icon") && !child.get("icon").isNull()
                                ? child.get("icon").asText() : null);
                        childRequest.setDisplayOrder(child.get("displayOrder").asInt());
                        childRequest.setPermissionName(child.has("permissionName") && !child.get("permissionName").isNull()
                                ? child.get("permissionName").asText() : null);
                        childRequest.setParentId(menuId);
                        childRequest.setActive(true);

                        Long childId = menuApplicationService.createMenuItem(childRequest).id();

                        if (childRequest.getPermissionName() != null) {
                            permissionService.setMenuId(childRequest.getPermissionName(), childId);
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
