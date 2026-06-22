package com.channel360.common.constants;

import com.channel360.role.enums.RoleName;

public final class AppConstants {

    private AppConstants() {}

    public static final String ROLE_SUPER_ADMIN = RoleName.ROLE_SUPER_ADMIN.name();
    public static final String ROLE_ADMIN = RoleName.ROLE_ADMIN.name();
    public static final String ROLE_USER = RoleName.ROLE_USER.name();

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";

    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";

    public static final String TOKEN_TYPE = "Bearer";

    public static final String SECTION_TYPE_HERO_BANNER = "hero_banner";
    public static final String SECTION_TYPE_PRODUCT_JOURNEY = "product_journey";
    public static final String SECTION_TYPE_PLATFORM_CAPABILITIES = "platform_capabilities";
    public static final String SECTION_TYPE_BENEFITS = "benefits";
    public static final String SECTION_TYPE_CONTACT = "contact";
    public static final String SECTION_TYPE_FOOTER = "footer";
    public static final String SECTION_TYPE_ANNOUNCEMENT = "announcement";
    public static final String SECTION_TYPE_INFO_BLOCK = "info_block";
    public static final String SECTION_TYPE_PROMOTION = "promotion";
    public static final String SECTION_TYPE_IMAGE_CARD = "image_card";
    public static final String SECTION_TYPE_FAQ = "faq";

    public static final String[] VALID_SECTION_TYPES = {
        SECTION_TYPE_HERO_BANNER, SECTION_TYPE_PRODUCT_JOURNEY,
        SECTION_TYPE_PLATFORM_CAPABILITIES, SECTION_TYPE_BENEFITS,
        SECTION_TYPE_CONTACT, SECTION_TYPE_FOOTER,
        SECTION_TYPE_ANNOUNCEMENT, SECTION_TYPE_INFO_BLOCK,
        SECTION_TYPE_PROMOTION, SECTION_TYPE_IMAGE_CARD,
        SECTION_TYPE_FAQ
    };
}
