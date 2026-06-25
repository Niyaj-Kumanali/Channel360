package com.channel360.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import org.springframework.core.annotation.Order;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:db/procedures/*.sql");

            for (Resource resource : resources) {
                String filename = resource.getFilename();
                log.info("Executing procedure file: {}", filename);
                String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                executeStatements(content);
                log.info("Completed procedure file: {}", filename);
            }
        } catch (Exception e) {
            log.error("Failed to initialize database procedures", e);
        }
    }

    private void executeStatements(String sql) {
        String[] procedures = sql.split("\\$\\$\\s*;");
        for (String stmt : procedures) {
            String trimmed = stmt.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            try {
                jdbcTemplate.execute(trimmed + "$$;");
            } catch (Exception e) {
                log.error("Failed to execute statement: {}", trimmed.substring(0, Math.min(100, trimmed.length())), e);
            }
        }
    }
}
