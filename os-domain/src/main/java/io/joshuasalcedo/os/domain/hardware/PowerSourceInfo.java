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