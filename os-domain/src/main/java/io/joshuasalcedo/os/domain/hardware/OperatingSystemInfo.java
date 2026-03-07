package io.joshuasalcedo.os.domain.hardware;

import io.joshuasalcedo.os.domain.Manufacturer;

import java.time.Instant;
import java.util.Objects;

/**
 * Value object representing operating system information.
 */
public record OperatingSystemInfo(
        String family,
        Manufacturer manufacturer,
        String versionInfo,
        String buildNumber,
        String codeName,
        int bitness,
        int processCount,
        int threadCount,
        Instant bootTime,
        long uptimeSeconds,
        boolean elevated
) implements OSHardwareObject {

    public OperatingSystemInfo {
        Objects.requireNonNull(family, "OS family must not be null");
    }

    public String uptimeFormatted() {
        long days = uptimeSeconds / 86400;
        long hours = (uptimeSeconds % 86400) / 3600;
        long minutes = (uptimeSeconds % 3600) / 60;
        if (days > 0) {
            return String.format("%dd %dh %dm", days, hours, minutes);
        }
        return String.format("%dh %dm", hours, minutes);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s (%d-bit), uptime: %s",
                manufacturer, family, versionInfo, bitness, uptimeFormatted());
    }
}