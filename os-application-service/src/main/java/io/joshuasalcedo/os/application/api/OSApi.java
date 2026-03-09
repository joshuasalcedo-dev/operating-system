package io.joshuasalcedo.os.application.api;

import io.joshuasalcedo.os.domain.computer.OSEnvironment;
import io.joshuasalcedo.os.domain.hardware.OperatingSystemInfo;

import java.util.List;
import java.util.Map;

/**
 * OsAPI class.
 *
 * @author JoshuaSalcedo
 * @since 3/9/2026 7:41 AM
 */

public interface OsAPI {

	OperatingSystemInfo getOperatingSystemInfo();

	OSEnvironment getOSEnvironment();

	String operatingSystemName();

	Map<String,List<String>> getEnvironmentVariables();

	void setEnvironmentVariable(String name, String value);

	void unsetEnvironmentVariable(String name);

	Map<String,String> getAlias();

	void setAlias(String name, String value);

}
