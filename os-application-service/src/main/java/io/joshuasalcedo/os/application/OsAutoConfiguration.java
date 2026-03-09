package io.joshuasalcedo.os.application;

import io.joshuasalcedo.os.application.api.HardwareAPI;
import io.joshuasalcedo.os.application.api.NetworkAPI;
import io.joshuasalcedo.os.application.api.OsAPI;
import io.joshuasalcedo.os.application.api.SshAPI;
import io.joshuasalcedo.os.application.api.hardware.HardwareConfiguration;
import io.joshuasalcedo.os.application.api.network.NetworkConfiguration;
import io.joshuasalcedo.os.application.api.os.OsConfiguration;
import io.joshuasalcedo.os.application.api.ssh.SshConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

/**
 * Spring Boot auto-configuration for the OS library.
 * <p>
 * Imports per-package {@code @Configuration} classes that conditionally
 * register beans based on property flags ({@code os.hardware.enabled},
 * {@code os.network.enabled}, {@code os.operating-system.enabled},
 * {@code os.ssh.enabled}).
 *
 * @author JoshuaSalcedo
 * @since 3/9/2026
 */
@AutoConfiguration
@Import(value = {
	HardwareConfiguration.class,
	NetworkConfiguration.class,
	OsConfiguration.class,
	SshConfiguration.class
})
@Slf4j
@RequiredArgsConstructor
public class OsAutoConfiguration {

	private final ApplicationContext context;

	@PostConstruct
	void init() {
		log.info("──────────────────────────────────────────────────────");
		log.info("OS Library Auto-Configuration");
		log.info("──────────────────────────────────────────────────────");

		logBeanStatus("HardwareAPI", HardwareAPI.class, "os.hardware.enabled");
		logBeanStatus("NetworkAPI",  NetworkAPI.class,  "os.network.enabled");
		logBeanStatus("OsAPI",       OsAPI.class,       "os.operating-system.enabled");
		logBeanStatus("SshAPI",      SshAPI.class,       "os.ssh.enabled");

		log.info("──────────────────────────────────────────────────────");
	}

	private void logBeanStatus(String name, Class<?> type, String property) {
		if (context.getBeanNamesForType(type).length > 0) {
			log.info("  {} : ENABLED", name);
		} else {
			log.info("  {} : DISABLED — to enable, add '{}=true' to your application.properties or application.yml", name, property);
		}
	}
}
