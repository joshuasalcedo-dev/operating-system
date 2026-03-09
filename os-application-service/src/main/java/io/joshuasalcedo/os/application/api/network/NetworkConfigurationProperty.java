package io.joshuasalcedo.os.application.api.network;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "os.network")
class NetworkConfigurationProperty {

	private boolean enabled = false;

	public boolean enabled() {
		return enabled;
	}
	public boolean isEnabled() {
		return enabled;
	}
}
