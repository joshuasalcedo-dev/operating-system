package io.joshuasalcedo.os.application.api.os;

import io.joshuasalcedo.os.application.api.OsAPI;
import io.joshuasalcedo.os.domain.computer.OSEnvironment;
import io.joshuasalcedo.os.domain.hardware.OperatingSystemInfo;
import oshi.SystemInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Package-private OSHI-backed implementation of {@link OsAPI}.
 *
 * @author JoshuaSalcedo
 * @since 3/9/2026
 */
class DefaultOsAPI implements OsAPI {

	private final OperatingSystemAPI operatingSystemAPI;

	DefaultOsAPI() {
		SystemInfo systemInfo = new SystemInfo();
		this.operatingSystemAPI = OperatingSystemAPI.oshi(systemInfo);
	}

	@Override
	public OperatingSystemInfo getOperatingSystemInfo() {
		return operatingSystemAPI.getOperatingSystem();
	}

	@Override
	public OSEnvironment getOSEnvironment() {
		return getOperatingSystemInfo().osEnvironment();
	}

	@Override
	public String operatingSystemName() {
		return getOperatingSystemInfo().family();
	}

	@Override
	public Map<String, List<String>> getEnvironmentVariables() {
		Map<String, List<String>> result = new LinkedHashMap<>();
		System.getenv().forEach((key, value) -> {
			if (key.equalsIgnoreCase("PATH") || value.contains(":") || value.contains(";")) {
				String separator = value.contains(";") ? ";" : ":";
				result.put(key, List.of(value.split(separator)));
			} else {
				result.put(key, List.of(value));
			}
		});
		return Collections.unmodifiableMap(result);
	}

	@Override
	public void setEnvironmentVariable(String name, String value) {
		throw new UnsupportedOperationException("setEnvironmentVariable not yet implemented");
	}

	@Override
	public void unsetEnvironmentVariable(String name) {
		throw new UnsupportedOperationException("unsetEnvironmentVariable not yet implemented");
	}

	@Override
	public Map<String, String> getAlias() {
		throw new UnsupportedOperationException("getAlias not yet implemented");
	}

	@Override
	public void setAlias(String name, String value) {
		throw new UnsupportedOperationException("setAlias not yet implemented");
	}
}
