package com.channel360.common.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleSeeder roleSeeder;
    private final PermissionSeeder permissionSeeder;
    private final RolePermissionSeeder rolePermissionSeeder;
    private final MenuSeeder menuSeeder;
    private final HomepageSectionSeeder homepageSectionSeeder;
    private final HomepagePopUpSeeder homepagePopUpSeeder;
    private final UserSeeder userSeeder;

    @Override
    public void run(String... args) {
        log.info("=== Data seeding started ===");

        try { roleSeeder.seed(); } catch (Exception e) { log.error("RoleSeeder failed", e); }
        try { permissionSeeder.seed(); } catch (Exception e) { log.error("PermissionSeeder failed", e); }
        try { rolePermissionSeeder.seed(); } catch (Exception e) { log.error("RolePermissionSeeder failed", e); }
        try { menuSeeder.seed(); } catch (Exception e) { log.error("MenuSeeder failed", e); }
        try { homepageSectionSeeder.seed(); } catch (Exception e) { log.error("HomepageSectionSeeder failed", e); }
        try { homepagePopUpSeeder.seed(); } catch (Exception e) { log.error("HomepagePopUpSeeder failed", e); }
        try { userSeeder.seed(); } catch (Exception e) { log.error("UserSeeder failed", e); }

        log.info("=== Data seeding completed ===");
    }
}
