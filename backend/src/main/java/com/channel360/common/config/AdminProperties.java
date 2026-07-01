package com.channel360.common.config;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Builder
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
        if (firstName == null) firstName = "Niyaz";
        if (lastName == null) lastName = "Kumanali";
        if (mobileNumber == null) mobileNumber = "";
    }
}
