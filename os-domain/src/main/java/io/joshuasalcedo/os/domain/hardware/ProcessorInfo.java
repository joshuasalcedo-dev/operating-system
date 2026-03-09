package io.joshuasalcedo.os.domain.hardware;

import io.joshuasalcedo.os.domain.Manufacturer;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Value object representing CPU/Processor information.
 * Immutable snapshot of processor state from OSHI.
 */
public record ProcessorInfo(
        String name,
        Manufacturer manufacturer,
        String identifier,
        String processorId,
        String microarchitecture,
        int physicalPackages,
        int physicalCores,
        int logicalProcessors,
        long vendorFrequency,
        long maxFrequency,
        List<CacheInfo> caches,
        List<CoreTopology> topology
) implements OSHardwareObject {

    public ProcessorInfo {
        Objects.requireNonNull(name, "Processor name must not be null");
        Objects.requireNonNull(manufacturer, "Manufacturer must not be null");
        caches = caches != null ? Collections.unmodifiableList(caches) : List.of();
        topology = topology != null ? Collections.unmodifiableList(topology) : List.of();
    }

    public String vendorFrequencyFormatted() {
        return formatHertz(vendorFrequency);
    }

    public String maxFrequencyFormatted() {
        return formatHertz(maxFrequency);
    }

    /** True if the max frequency is higher than the vendor base frequency (boost/turbo capable). */
    public boolean isBoostCapable() {
        return maxFrequency > vendorFrequency;
    }

    /** True if this processor reports more logical processors than physical cores (HyperThreading / SMT). */
    public boolean isHyperThreaded() {
        return logicalProcessors > physicalCores;
    }

    /** All caches at the given level (1, 2, or 3). */
    public List<CacheInfo> cachesAtLevel(int level) {
        return caches.stream()
                .filter(c -> c.level() == level)
                .toList();
    }

    /** All NUMA nodes present on this processor. */
    public List<Integer> numaNodes() {
        return topology.stream()
                .map(CoreTopology::numaNode)
                .distinct()
                .sorted()
                .toList();
    }

    /** True if this processor has more than one NUMA node. */
    public boolean isNuma() {
        return numaNodes().size() > 1;
    }

    private static String formatHertz(long hertz) {
        if (hertz <= 0) return "N/A";
        if (hertz >= 1_000_000_000L) {
            return String.format(Locale.ROOT, "%.2f GHz", hertz / 1_000_000_000.0);
        } else if (hertz >= 1_000_000L) {
            return String.format(Locale.ROOT, "%.2f MHz", hertz / 1_000_000.0);
        }
        return hertz + " Hz";
    }

    /**
     * Value object for processor cache information.
     */
    public record CacheInfo(
            int level,
            String type,
            long cacheSize,
            int associativity,
            int lineSize
    ) {
        public String cacheSizeFormatted() {
            if (cacheSize >= 1_048_576L) {
                return String.format(Locale.ROOT, "%d MB", cacheSize / 1_048_576L);
            } else if (cacheSize >= 1024L) {
                return String.format(Locale.ROOT, "%d KB", cacheSize / 1024L);
            }
            return cacheSize + " B";
        }

        @Override
        public String toString() {
            return String.format("L%d %s %s, %d-way, %d byte line",
                    level, type, cacheSizeFormatted(), associativity, lineSize);
        }
    }

    /**
     * Value object for individual core topology.
     */
    public record CoreTopology(
            String logicalProcessors,
            String efficiencyClass,
            int physicalProcessorNumber,
            int physicalPackageNumber,
            int numaNode,
            int processorGroup
    ) {
        @Override
        public String toString() {
            return String.format("Core %d [%s] %s pkg=%d numa=%d",
                    physicalProcessorNumber, logicalProcessors, efficiencyClass,
                    physicalPackageNumber, numaNode);
        }
    }

    @Override
    public String toString() {
        return String.format("%s (%d cores / %d threads) - %s",
                name, physicalCores, logicalProcessors, microarchitecture);
    }
}