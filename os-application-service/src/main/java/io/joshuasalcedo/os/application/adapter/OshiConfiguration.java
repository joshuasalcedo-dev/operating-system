package io.joshuasalcedo.os.application.adapter;

import oshi.SystemInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OshiConfiguration {

    @Bean
    public SystemInfo systemInfo() {
        return new SystemInfo();
    }
}
