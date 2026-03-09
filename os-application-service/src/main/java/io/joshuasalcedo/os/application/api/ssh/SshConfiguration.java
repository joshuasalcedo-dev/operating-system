package io.joshuasalcedo.os.application.api.ssh;

import io.joshuasalcedo.os.application.api.SshAPI;
import io.joshuasalcedo.os.application.api.ssh.client.SshClientAPI;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SshConfigurationProperty.class)
@Slf4j
public class SshConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "os.ssh", name = "enabled", havingValue = "true", matchIfMissing = false)
    public SshAPI sshAPI() {
        log.info("✅ SshAPI is ENABLED.");
        return new DefaultSshAPI(SshClientAPI.SshConnectionConfig.defaults());
    }

    @PostConstruct
    void init() {
        log.debug("SshConfiguration initialized. Bean creation depends on 'os.ssh.enabled'.");
    }
}
