package io.joshuasalcedo.os.application.api.hardware;

import io.joshuasalcedo.os.application.api.HardwareAPI;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = "oshi.SystemInfo")
@EnableConfigurationProperties(HardwareConfigurationProperty.class)
@Slf4j
public class HardwareConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "os.hardware", name = "enabled", havingValue = "true", matchIfMissing = false)
    public HardwareAPI hardwareAPI() {
        log.info("✅ HardwareAPI is ENABLED.");
        return new DefaultHardwareAPI();
    }

    @PostConstruct
    void init() {
        log.debug("HardwareConfiguration initialized. Bean creation depends on 'os.hardware.enabled'.");
    }
}
