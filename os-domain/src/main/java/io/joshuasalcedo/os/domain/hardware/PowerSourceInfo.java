package io.joshuasalcedo.os.domain.hardware;

import io.joshuasalcedo.os.domain.Manufacturer;

import java.util.Locale;
import java.util.Objects;

/**
 * Value object representing a power source/battery.
 */
public record PowerSourceInfo(
        String name,
        Manufacturer manufacturer,
        String chemistry,
        double remainingCapacityPercent,
        double timeRemainingEstimated,
        boolean charging,
        boolean discharging,
        boolean powerOnLine
) implements OSHardwareObject {

    public PowerSourceInfo {
        Objects.requireNonNull(name, "Power source name must not be null");
    }

    public String remainingFormatted() {
        return String.format(Locale.ROOT, "%.1f%%", remainingCapacityPercent * 100);
    }

    /** True if this battery is critically low (below 10%). */
    public boolean isCriticallyLow() {
        return remainingCapacityPercent < 0.10;
    }

    /** True if this battery is low (below 20%). */
    public boolean isLow() {
        return remainingCapacityPercent < 0.20;
    }

    /** True if this battery is fully charged (>= 99%). */
    public boolean isFullyCharged() {
        return remainingCapacityPercent >= 0.99;
    }

    /** True if the time remaining estimate is still being calculated. */
    public boolean isCalculatingTimeRemaining() {
        return timeRemainingEstimated < 0;
    }

    public String timeRemainingFormatted() {
        if (timeRemainingEstimated < 0) return "Calculating...";
        long seconds = (long) timeRemainingEstimated;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        return String.format("%dh %dm", hours, minutes);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s",
                name, remainingFormatted(), charging ? "(charging)" : "(discharging)");
    }
}