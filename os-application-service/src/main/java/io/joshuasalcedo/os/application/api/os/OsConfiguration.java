package io.joshuasalcedo.os.application.api.os;

import io.joshuasalcedo.os.application.api.OsAPI;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = "oshi.SystemInfo")
@EnableConfigurationProperties(OsConfigurationProperty.class)
@Slf4j
public class OsConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "os.operating-system", name = "enabled", havingValue = "true", matchIfMissing = false)
    public OsAPI osAPI() {
        log.info("✅ OsAPI is ENABLED.");
        return new DefaultOsAPI();
    }

    @PostConstruct
    void init() {
        log.debug("OsConfiguration initialized. Bean creation depends on 'os.operating-system.enabled'.");
    }
}
