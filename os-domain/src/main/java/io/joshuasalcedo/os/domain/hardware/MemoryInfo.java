package io.joshuasalcedo.os.domain.hardware;

import io.joshuasalcedo.os.domain.Manufacturer;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Value object representing system memory information.
 */
public record MemoryInfo(
        long totalPhysical,
        long availablePhysical,
        long totalVirtual,
        long virtualInUse,
        long swapTotal,
        long swapUsed,
        List<PhysicalMemoryModule> modules
) implements OSHardwareObject {

    public MemoryInfo {
        modules = modules != null ? Collections.unmodifiableList(modules) : List.of();
    }

    public long usedPhysical() {
        return totalPhysical - availablePhysical;
    }

    public double usagePercent() {
        return totalPhysical > 0 ? 100.0 * usedPhysical() / totalPhysical : 0;
    }

    public String totalFormatted() {
        return formatBytes(totalPhysical);
    }

    public String availableFormatted() {
        return formatBytes(availablePhysical);
    }

    /**
     * Value object for individual physical memory sticks/modules.
     */
    public record PhysicalMemoryModule(
            String bankLabel,
            long capacity,
            long clockSpeed,
            Manufacturer manufacturer,
            String memoryType
    ) {
        public String capacityFormatted() {
            return formatBytes(capacity);
        }

        public String clockSpeedFormatted() {
            if (clockSpeed <= 0) return "N/A";
            return String.format(Locale.ROOT, "%d MHz", clockSpeed / 1_000_000L);
        }

        @Override
        public String toString() {
            return String.format("%s %s %s @ %s (%s)",
                    bankLabel, manufacturer, capacityFormatted(), clockSpeedFormatted(), memoryType);
        }
    }

    private static String formatBytes(long bytes) {
        if (bytes >= 1_073_741_824L) {
            return String.format(Locale.ROOT, "%.1f GB", bytes / 1_073_741_824.0);
        } else if (bytes >= 1_048_576L) {
            return String.format(Locale.ROOT, "%.1f MB", bytes / 1_048_576.0);
        }
        return String.format(Locale.ROOT, "%d KB", bytes / 1024);
    }

    @Override
    public String toString() {
        return String.format("Memory: %s / %s (%.1f%% used), %d module(s)",
                formatBytes(usedPhysical()), totalFormatted(), usagePercent(), modules.size());
    }
}