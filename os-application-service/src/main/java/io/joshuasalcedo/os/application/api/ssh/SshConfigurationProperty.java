package io.joshuasalcedo.os.application.api.ssh;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "os.ssh")
class SshConfigurationProperty {

	private boolean enabled = false;

	public boolean enabled() {
		return enabled;
	}
	public boolean isEnabled() {
		return enabled;
	}
}
