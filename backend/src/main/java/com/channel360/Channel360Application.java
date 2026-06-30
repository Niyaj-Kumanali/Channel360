package com.channel360;

import com.channel360.common.config.AdminProperties;
import com.channel360.common.config.SuperAdminProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties({AdminProperties.class, SuperAdminProperties.class})
@EnableScheduling
public class Channel360Application {

    public static void main(String[] args) {
        SpringApplication.run(Channel360Application.class, args);
    }
}
