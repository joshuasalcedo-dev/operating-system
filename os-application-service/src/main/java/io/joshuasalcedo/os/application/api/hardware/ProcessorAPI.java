package io.joshuasalcedo.os.application.api.hardware;

import io.joshuasalcedo.os.domain.hardware.ProcessorInfo;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for querying processor/CPU information.
 */
interface ProcessorAPI {

    // ── Core ──────────────────────────────────────────────────────────────────

    /** Returns all detected physical processor packages on the system. */
    List<ProcessorInfo> getProcessors();

    /** Returns caches for a given processor filtered by cache level (1, 2, 3). */
    List<ProcessorInfo.CacheInfo> getCaches(ProcessorInfo processor, int level);

    // ── Package / Core / Thread counts ───────────────────────────────────────

    /** Total number of physical CPU packages (sockets) installed. */
    default int totalPackages() {
        return getProcessors().stream()
                .mapToInt(ProcessorInfo::physicalPackages)
                .sum();
    }

    /** Total physical cores across all packages. */
    default int totalPhysicalCores() {
        return getProcessors().stream()
                .mapToInt(ProcessorInfo::physicalCores)
                .sum();
    }

    /** Total logical processors (hardware threads) across all packages. */
    default int totalLogicalProcessors() {
        return getProcessors().stream()
                .mapToInt(ProcessorInfo::logicalProcessors)
                .sum();
    }

    /** True if any processor reports more logical processors than physical cores (HyperThreading / SMT). */
    default boolean isHyperThreadingEnabled() {
        return getProcessors().stream().anyMatch(ProcessorInfo::isHyperThreaded);
    }

    // ── Frequency ─────────────────────────────────────────────────────────────

    /** Processor with the highest advertised max frequency. */
    default Optional<ProcessorInfo> fastestProcessor() {
        return getProcessors().stream()
                .max(java.util.Comparator.comparingLong(ProcessorInfo::maxFrequency));
    }

    /** True if the max frequency reported is higher than the vendor base frequency (boost/turbo capable). */
    default boolean isBoostCapable(ProcessorInfo processor) {
        return processor.isBoostCapable();
    }

    // ── Architecture ──────────────────────────────────────────────────────────

    /** All distinct microarchitectures present (relevant on hybrid CPUs like Intel P+E core designs). */
    default List<String> microarchitectures() {
        return getProcessors().stream()
                .map(ProcessorInfo::microarchitecture)
                .distinct()
                .toList();
    }

    /** True if the system has heterogeneous cores (e.g. Intel Alder Lake P-cores + E-cores). */
    default boolean isHybridArchitecture() {
        return microarchitectures().size() > 1;
    }

    // ── Cache ─────────────────────────────────────────────────────────────────

    /** All L1 caches for the given processor. */
    default List<ProcessorInfo.CacheInfo> l1Caches(ProcessorInfo processor) {
        return getCaches(processor, 1);
    }

    /** All L2 caches for the given processor. */
    default List<ProcessorInfo.CacheInfo> l2Caches(ProcessorInfo processor) {
        return getCaches(processor, 2);
    }

    /** All L3 caches for the given processor. */
    default List<ProcessorInfo.CacheInfo> l3Caches(ProcessorInfo processor) {
        return getCaches(processor, 3);
    }

    /** Total L3 cache size in bytes across all processors. */
    default long totalL3CacheBytes() {
        return getProcessors().stream()
                .flatMap(p -> getCaches(p, 3).stream())
                .mapToLong(ProcessorInfo.CacheInfo::cacheSize)
                .sum();
    }

    // ── Topology ──────────────────────────────────────────────────────────────

    /** All core topology entries for a given processor. */
    default List<ProcessorInfo.CoreTopology> topology(ProcessorInfo processor) {
        return processor.topology();
    }

    /** NUMA nodes present on the given processor. */
    default List<Integer> numaNodes(ProcessorInfo processor) {
        return processor.numaNodes();
    }

    /** True if the processor has more than one NUMA node (relevant for multi-socket or NUMA-aware servers). */
    default boolean isNuma(ProcessorInfo processor) {
        return processor.isNuma();
    }

    // ── Lookup ────────────────────────────────────────────────────────────────

    /** Find a processor by its processor ID string. */
    default Optional<ProcessorInfo> findByProcessorId(String processorId) {
        return getProcessors().stream()
                .filter(p -> p.processorId().equalsIgnoreCase(processorId))
                .findFirst();
    }

}