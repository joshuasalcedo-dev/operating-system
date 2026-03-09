package io.joshuasalcedo.os.application.api.network;

import io.joshuasalcedo.os.application.api.NetworkAPI;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(NetworkConfigurationProperty.class)
@Slf4j
public class NetworkConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "os.network", name = "enabled", havingValue = "true", matchIfMissing = false)
    public NetworkAPI networkAPI() {
        log.info("✅ NetworkAPI is ENABLED.");
        return new DefaultNetworkAPI();
    }

    @PostConstruct
    void init() {
        log.debug("NetworkConfiguration initialized. Bean creation depends on 'os.network.enabled'.");
    }
}
