package com.channel360;

import com.channel360.common.config.AdminProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AdminProperties.class)
public class Channel360Application {

    public static void main(String[] args) {
        SpringApplication.run(Channel360Application.class, args);
    }
}
