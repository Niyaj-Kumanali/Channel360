package com.channel360.homepage.infrastructure;

import com.channel360.homepage.api.request.HomepageSectionRequest;
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
public class HomepageSectionSeeder {

    private final HomepageService homepageService;
    private final ObjectMapper objectMapper;

    public void seed() {
        try {
            String json = new String(
                    new ClassPathResource("db/seed/homepage-sections.json").getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );
            List<Map<String, Object>> sections = objectMapper.readValue(json,
                    new TypeReference<List<Map<String, Object>>>() {});

            for (Map<String, Object> entry : sections) {
                HomepageSectionRequest request = HomepageSectionRequest.builder()
                        .sectionName((String) entry.get("sectionName"))
                        .sectionType((String) entry.get("sectionType"))
                        .title((String) entry.get("title"))
                        .subtitle((String) entry.get("subtitle"))
                        .description((String) entry.get("description"))
                        .buttonText((String) entry.get("buttonText"))
                        .buttonUrl((String) entry.get("buttonUrl"))
                        .displayOrder((Integer) entry.get("displayOrder"))
                        .active((Boolean) entry.get("active"))
                        .build();
                try {
                    homepageService.saveSection(request, "SYSTEM");
                    log.debug("Seeded homepage section: {}", entry.get("sectionName"));
                } catch (Exception e) {
                    log.debug("Homepage section already exists: {}", entry.get("sectionName"));
                }
            }
        } catch (Exception e) {
            log.error("Failed to seed homepage sections", e);
        }
    }
}
