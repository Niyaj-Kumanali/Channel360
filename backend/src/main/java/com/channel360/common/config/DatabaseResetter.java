package com.channel360.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@Order(0)
public class DatabaseResetter implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final boolean reset;

    public DatabaseResetter(JdbcTemplate jdbcTemplate, @Value("${app.reset:false}") boolean reset) {
        this.jdbcTemplate = jdbcTemplate;
        this.reset = reset;
    }

    @Override
    public void run(String... args) {
        if (!reset) {
            log.info("Database reset skipped (app.reset=false)");
            return;
        }

        log.warn("==============================================");
        log.warn("DATABASE RESET ENABLED — Dropping all objects");
        log.warn("==============================================");

        try {
            for (String file : new String[]{"db/reset.sql", "db/schema.sql", "db/seed.sql"}) {
                ClassPathResource resource = new ClassPathResource(file);
                String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                log.info("Executing: {}", file);
                executeStatements(content);
            }
            log.info("Database reset completed successfully");
        } catch (Exception e) {
            log.error("Database reset failed", e);
        }
    }

    private void executeStatements(String sql) {
        String[] statements = sql.split(";");
        for (String stmt : statements) {
            String trimmed = stmt.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                continue;
            }
            try {
                jdbcTemplate.execute(trimmed);
            } catch (Exception e) {
                log.warn("Statement failed (may be expected): {} - {}", trimmed.substring(0, Math.min(120, trimmed.length())), e.getMessage());
            }
        }
    }
}
