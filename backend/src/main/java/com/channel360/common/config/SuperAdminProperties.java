package com.channel360.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.super-admin")
public record SuperAdminProperties(
        String email,
        String password,
        String firstName,
        String lastName,
        String mobileNumber,
        boolean skip
) {
    public SuperAdminProperties {
        if (email == null) email = "";
        if (password == null) password = "";
        if (firstName == null) firstName = "Niyaz";
        if (lastName == null) lastName = "Kumanali";
        if (mobileNumber == null) mobileNumber = "";
    }
}
