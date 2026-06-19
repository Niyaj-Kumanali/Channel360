package com.channel360.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.admin")
public record AdminProperties(
        String email,
        String password,
        String firstName,
        String lastName,
        String mobileNumber,
        boolean skip
) {
    public AdminProperties {
        if (email == null) email = "";
        if (password == null) password = "";
        if (firstName == null) firstName = "Admin";
        if (lastName == null) lastName = "User";
        if (mobileNumber == null) mobileNumber = "";
    }
}
