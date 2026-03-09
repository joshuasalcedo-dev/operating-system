package io.joshuasalcedo.os.application.api.os;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "os.operating-system")
class OsConfigurationProperty {

	private boolean enabled = false;

	public boolean enabled() {
		return enabled;
	}
	public boolean isEnabled() {
		return enabled;
	}
}
