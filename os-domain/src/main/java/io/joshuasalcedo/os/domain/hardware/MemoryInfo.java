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

    /** True if used memory exceeds the given threshold percentage (e.g. 90.0 for 90%). */
    public boolean isUnderPressure(double thresholdPercent) {
        return usagePercent() >= thresholdPercent;
    }

    /** True if memory usage is critically high (above 90%). */
    public boolean isCritical() {
        return isUnderPressure(90.0);
    }

    /** True if any swap is currently being used. */
    public boolean isSwapping() {
        return swapUsed > 0;
    }

    /** Swap usage as a percentage (0–100). */
    public double swapUsagePercent() {
        return swapTotal > 0 ? 100.0 * swapUsed / swapTotal : 0.0;
    }

    /** Number of memory modules installed. */
    public int moduleCount() {
        return modules.size();
    }

    /** All distinct memory types present (e.g. DDR4, DDR5). */
    public List<String> memoryTypes() {
        return modules.stream()
                .map(PhysicalMemoryModule::memoryType)
                .distinct()
                .toList();
    }

    /** All distinct clock speeds present across modules, in Hz. */
    public List<Long> clockSpeeds() {
        return modules.stream()
                .map(PhysicalMemoryModule::clockSpeed)
                .distinct()
                .sorted()
                .toList();
    }

    /** True if modules are running at different clock speeds (mismatched kit). */
    public boolean hasMismatchedSpeeds() {
        return clockSpeeds().size() > 1;
    }

    /** Total capacity of all installed modules in bytes. */
    public long totalModuleCapacity() {
        return modules.stream()
                .mapToLong(PhysicalMemoryModule::capacity)
                .sum();
    }

    /** Find a module by its bank label (e.g. "ChannelA-DIMM0"). */
    public java.util.Optional<PhysicalMemoryModule> findModuleByBank(String bankLabel) {
        return modules.stream()
                .filter(m -> m.bankLabel().equalsIgnoreCase(bankLabel))
                .findFirst();
    }

    /** Modules from a specific manufacturer. */
    public List<PhysicalMemoryModule> modulesByManufacturer(String manufacturer) {
        return modules.stream()
                .filter(m -> m.manufacturer() != null &&
                             m.manufacturer().toString().equalsIgnoreCase(manufacturer))
                .toList();
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