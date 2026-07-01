package com.channel360.common.seeder;

import com.channel360.homepage.api.request.HomepagePopupRequest;
import com.channel360.homepage.application.HomepageService;
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
public class HomepagePopUpSeeder {

    private final HomepageService homepageService;
    private final ObjectMapper objectMapper;

    public void seed() {
        try {
            if (!homepageService.getAllPopups().isEmpty()) {
                log.info("Homepage popups already exist, skipping seed");
                return;
            }

            String json = new String(
                    new ClassPathResource("db/seed/homepage-popups.json").getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );
            List<Map<String, Object>> popups = objectMapper.readValue(json,
                    new TypeReference<List<Map<String, Object>>>() {});

            for (Map<String, Object> entry : popups) {
                HomepagePopupRequest request = HomepagePopupRequest.builder()
                        .title((String) entry.get("title"))
                        .description((String) entry.get("description"))
                        .ctaButtonText((String) entry.get("ctaButtonText"))
                        .ctaUrl((String) entry.get("ctaUrl"))
                        .priority((Integer) entry.get("priority"))
                        .active((Boolean) entry.get("active"))
                        .build();

                homepageService.savePopup(request, "system");
                log.debug("Seeded homepage popup: {}", entry.get("title"));
            }
        } catch (Exception e) {
            log.error("Failed to seed homepage popups", e);
        }
    }
}
