package io.joshuasalcedo.os.application.api.hardware;


import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * HardwareConfigurationProperty class.
 *
 * @author JoshuaSalcedo
 * @since 3/9/2026 8:38 AM
 */
@Getter
@ConfigurationProperties(prefix = "os.hardware")
class  HardwareConfigurationProperty {

	private boolean enabled	= false;


}