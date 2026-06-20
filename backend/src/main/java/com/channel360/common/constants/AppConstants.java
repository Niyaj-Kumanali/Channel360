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
}
