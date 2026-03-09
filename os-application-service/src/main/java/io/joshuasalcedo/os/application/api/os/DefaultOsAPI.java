package io.joshuasalcedo.os.application.api.os;

import io.joshuasalcedo.os.application.api.OsAPI;
import io.joshuasalcedo.os.domain.computer.OSEnvironment;
import io.joshuasalcedo.os.domain.hardware.OperatingSystemInfo;

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
		this.operatingSystemAPI = new OshiOperatingSystemAPI(new oshi.SystemInfo());
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
		Objects.requireNonNull(name, "name");
		Objects.requireNonNull(value, "value");
		if (isWindows()) {
			exec("setx", name, value);
		} else {
			appendToShellProfile("export " + name + "=" + shellQuote(value));
		}
	}

	@Override
	public void unsetEnvironmentVariable(String name) {
		Objects.requireNonNull(name, "name");
		if (isWindows()) {
			exec("reg", "delete", "HKCU\\Environment", "/v", name, "/f");
		} else {
			appendToShellProfile("unset " + name);
		}
	}

	@Override
	public Map<String, String> getAlias() {
		if (isWindows()) {
			return Collections.emptyMap();
		}
		try {
			ProcessBuilder pb = new ProcessBuilder("bash", "-ic", "alias");
			pb.redirectErrorStream(true);
			Process process = pb.start();
			Map<String, String> aliases = new LinkedHashMap<>();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				reader.lines().forEach(line -> {
					if (line.startsWith("alias ")) {
						int eq = line.indexOf('=', 6);
						if (eq > 6) {
							String aliasName = line.substring(6, eq);
							String aliasValue = line.substring(eq + 1);
							if (aliasValue.length() >= 2
									&& ((aliasValue.startsWith("'") && aliasValue.endsWith("'"))
									|| (aliasValue.startsWith("\"") && aliasValue.endsWith("\"")))) {
								aliasValue = aliasValue.substring(1, aliasValue.length() - 1);
							}
							aliases.put(aliasName, aliasValue);
						}
					}
				});
			}
			process.waitFor();
			return Collections.unmodifiableMap(aliases);
		} catch (Exception e) {
			throw new RuntimeException("Failed to read shell aliases", e);
		}
	}

	@Override
	public void setAlias(String name, String value) {
		Objects.requireNonNull(name, "name");
		Objects.requireNonNull(value, "value");
		if (isWindows()) {
			exec("doskey", name + "=" + value);
		} else {
			appendToShellProfile("alias " + name + "=" + shellQuote(value));
		}
	}

	// ── Helpers ──────────────────────────────────────────────────────────────

	private static boolean isWindows() {
		return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
	}

	private static String shellQuote(String value) {
		return "'" + value.replace("'", "'\\''") + "'";
	}

	private static void appendToShellProfile(String line) {
		try {
			String home = System.getProperty("user.home");
			java.nio.file.Path profile = resolveShellProfile(home);
			java.nio.file.Files.writeString(profile, line + System.lineSeparator(),
					java.nio.file.StandardOpenOption.CREATE,
					java.nio.file.StandardOpenOption.APPEND);
		} catch (java.io.IOException e) {
			throw new RuntimeException("Failed to write to shell profile", e);
		}
	}

	private static java.nio.file.Path resolveShellProfile(String home) {
		String shell = System.getenv("SHELL");
		if (shell != null && shell.contains("zsh")) {
			return java.nio.file.Path.of(home, ".zshrc");
		}
		java.nio.file.Path bashrc = java.nio.file.Path.of(home, ".bashrc");
		if (java.nio.file.Files.exists(bashrc)) {
			return bashrc;
		}
		return java.nio.file.Path.of(home, ".profile");
	}

	private static void exec(String... command) {
		try {
			Process process = new ProcessBuilder(command)
					.redirectErrorStream(true)
					.start();
			int exitCode = process.waitFor();
			if (exitCode != 0) {
				String output;
				try (BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
					output = r.lines().collect(Collectors.joining("\n"));
				}
				throw new RuntimeException("Command failed (exit " + exitCode + "): "
						+ String.join(" ", command) + "\n" + output);
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("Failed to execute: " + String.join(" ", command), e);
		}
	}
}
