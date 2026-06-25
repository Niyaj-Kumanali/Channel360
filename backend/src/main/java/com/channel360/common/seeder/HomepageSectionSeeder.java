package com.channel360.common.seeder;

import com.channel360.homepage.api.HomepageSectionRequest;
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
            if (!homepageService.getAllSections().isEmpty()) {
                log.info("Homepage sections already exist, skipping seed");
                return;
            }

            String json = new String(
                    new ClassPathResource("db/seed/homepage-sections.json").getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );
            List<Map<String, Object>> sections = objectMapper.readValue(json,
                    new TypeReference<List<Map<String, Object>>>() {});

            for (Map<String, Object> entry : sections) {
                HomepageSectionRequest request = new HomepageSectionRequest();
                request.setSectionName((String) entry.get("sectionName"));
                request.setSectionType((String) entry.get("sectionType"));
                request.setTitle((String) entry.get("title"));
                request.setSubtitle((String) entry.get("subtitle"));
                request.setDescription((String) entry.get("description"));
                request.setButtonText((String) entry.get("buttonText"));
                request.setButtonUrl((String) entry.get("buttonUrl"));
                request.setDisplayOrder((Integer) entry.get("displayOrder"));
                request.setActive((Boolean) entry.get("active"));

                homepageService.saveSection(request, "system");
                log.debug("Seeded homepage section: {}", entry.get("sectionName"));
            }
        } catch (Exception e) {
            log.error("Failed to seed homepage sections", e);
        }
    }
}
