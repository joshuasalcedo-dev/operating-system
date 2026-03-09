package io.joshuasalcedo.os.application.api.os;

import io.joshuasalcedo.os.domain.computer.OSEnvironment;
import io.joshuasalcedo.os.domain.hardware.OperatingSystemInfo;

import java.time.Duration;
import java.time.Instant;

/**
 * Service interface for querying operating system information.
 */
interface OperatingSystemAPI {

    // ── Core ──────────────────────────────────────────────────────────────────

    /** Returns a snapshot of the current operating system state. */
    OperatingSystemInfo getOperatingSystem();

    // ── Identity ──────────────────────────────────────────────────────────────

    /** OS family name (e.g. "Windows", "Linux", "macOS"). */
    default String family() {
        return getOperatingSystem().family();
    }

    /** Full version string. */
    default String version() {
        return getOperatingSystem().versionInfo();
    }

    /** OS build number. */
    default String buildNumber() {
        return getOperatingSystem().buildNumber();
    }

    /** OS codename (e.g. "Jammy Jellyfish", "Ventura"). */
    default String codeName() {
        return getOperatingSystem().codeName();
    }

    /** Bitness of the OS (32 or 64). */
    default int bitness() {
        return getOperatingSystem().bitness();
    }

    /** True if the OS is 64-bit. */
    default boolean is64Bit() {
        return bitness() == 64;
    }

    // ── Platform detection ────────────────────────────────────────────────────

    /** True if the OS family is Windows. */
    default boolean isWindows() {
        return family().equalsIgnoreCase("Windows");
    }

    /** True if the OS family is Linux. */
    default boolean isLinux() {
        return family().equalsIgnoreCase("Linux");
    }

    /** True if the OS family is macOS. */
    default boolean isMacOs() {
        return family().equalsIgnoreCase("macOS") || family().equalsIgnoreCase("Mac OS X");
    }

    // ── Privileges ────────────────────────────────────────────────────────────

    /** True if the current process is running with elevated privileges (root / Administrator). */
    default boolean isElevated() {
        return getOperatingSystem().elevated();
    }

    // ── Uptime ────────────────────────────────────────────────────────────────

    /** System uptime in seconds since last boot. */
    default long uptimeSeconds() {
        return getOperatingSystem().uptimeSeconds();
    }

    /** System uptime as a {@link Duration}. */
    default Duration uptime() {
        return Duration.ofSeconds(uptimeSeconds());
    }

    /** The exact {@link Instant} the system was last booted. */
    default Instant bootTime() {
        return getOperatingSystem().bootTime();
    }

    /** True if the system has been running for longer than the given duration. */
    default boolean hasBeenUpLongerThan(Duration duration) {
        return uptime().compareTo(duration) > 0;
    }

    /** True if the system has been up for more than 30 days (often a sign it needs a reboot). */
    default boolean isOverdueForReboot() {
        return hasBeenUpLongerThan(Duration.ofDays(30));
    }

    // ── Processes & threads ───────────────────────────────────────────────────

    /** Current total process count. */
    default int processCount() {
        return getOperatingSystem().processCount();
    }

    /** Current total thread count. */
    default int threadCount() {
        return getOperatingSystem().threadCount();
    }

    /** Average threads per process. */
    default double avgThreadsPerProcess() {
        int procs = processCount();
        return procs > 0 ? (double) threadCount() / procs : 0.0;
    }

    // ── Environment detection ──────────────────────────────────────────────

    static OperatingSystemAPI oshi(oshi.SystemInfo systemInfo) {
        return new OshiOperatingSystemAPI(systemInfo);
    }

    /**
     * Detects the current {@link OSEnvironment} (physical, VM, container, WSL)
     * using the given system metadata.
     *
     * @param osFamily              OS family name (e.g. "Ubuntu", "Windows")
     * @param kernelBuildNumber     kernel build / version string
     * @param sysManufacturer       DMI system manufacturer
     * @param sysModel              DMI system model
     * @param firmwareManufacturer  DMI firmware manufacturer
     * @param baseboardManufacturer DMI baseboard manufacturer
     * @return the detected {@link OSEnvironment}
     */
    static OSEnvironment detectEnvironment(String osFamily,
                                           String kernelBuildNumber,
                                           String sysManufacturer,
                                           String sysModel,
                                           String firmwareManufacturer,
                                           String baseboardManufacturer) {
        return new OSEnvironmentDetector(
                osFamily, kernelBuildNumber,
                sysManufacturer, sysModel,
                firmwareManufacturer, baseboardManufacturer
        ).detect();
    }
}
