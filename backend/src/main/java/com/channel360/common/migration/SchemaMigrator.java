package com.channel360.common.migration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Order(0)
@RequiredArgsConstructor
public class SchemaMigrator implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Value("${app.seed:false}")
    private boolean seed;

    @Override
    public void run(String... args) {
        try {
            if (seed) {
                log.info("Clearing tables");
                jdbcTemplate.execute("DROP SCHEMA public CASCADE");
                jdbcTemplate.execute("CREATE SCHEMA public");
            }

            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS schema_migrations (
                    version VARCHAR(255) PRIMARY KEY,
                    filename VARCHAR(255) NOT NULL,
                    applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    checksum VARCHAR(64)
                )
            """);

            Set<String> applied = jdbcTemplate.queryForList(
                    "SELECT filename FROM schema_migrations ORDER BY version",
                    String.class
            ).stream().map(o -> (String) o).collect(Collectors.toSet());

            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:db/migrations/*.sql");
            Arrays.sort(resources, Comparator.comparing(Resource::getFilename));

            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename == null || applied.contains(filename)) continue;

                log.info("Applying migration: {}", filename);
                String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                String checksum = sha256(content);

                executeStatements(content);

                jdbcTemplate.update(
                        "INSERT INTO schema_migrations (version, filename, checksum) VALUES (?, ?, ?)",
                        filename.replaceAll("^V(\\d+)__.*", "$1"), filename, checksum
                );
                log.info("Applied migration: {}", filename);
            }
        } catch (Exception e) {
            log.error("Schema migration failed", e);
            throw new RuntimeException("Schema migration failed", e);
        }
    }

    private void executeStatements(String sql) {
        String[] statements = sql.split(";");
        for (String stmt : statements) {
            String trimmed = stmt.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("--")) continue;
            jdbcTemplate.execute(trimmed);
        }
    }

    private static String sha256(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(content.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            return "";
        }
    }
}
