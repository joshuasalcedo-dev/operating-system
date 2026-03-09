package io.joshuasalcedo.os.application.api.hardware;

import io.joshuasalcedo.os.domain.hardware.MemoryInfo;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for querying system memory information.
 */
interface MemoryAPI {

    // ── Core ──────────────────────────────────────────────────────────────────

    /** Returns the current memory snapshot. */
    MemoryInfo getMemory();

    // ── Physical memory ───────────────────────────────────────────────────────

    /** Total installed physical RAM in bytes. */
    default long totalBytes() {
        return getMemory().totalPhysical();
    }

    /** Currently available (free) physical RAM in bytes. */
    default long availableBytes() {
        return getMemory().availablePhysical();
    }

    /** Currently used physical RAM in bytes. */
    default long usedBytes() {
        return getMemory().usedPhysical();
    }

    /** Memory usage as a percentage (0–100). */
    default double usagePercent() {
        return getMemory().usagePercent();
    }

    /** True if used memory exceeds the given threshold percentage (e.g. 90.0 for 90%). */
    default boolean isUnderPressure(double thresholdPercent) {
        return getMemory().isUnderPressure(thresholdPercent);
    }

    /** True if memory usage is critically high (above 90%). */
    default boolean isCritical() {
        return getMemory().isCritical();
    }

    // ── Virtual memory ────────────────────────────────────────────────────────

    /** Total virtual address space in bytes. */
    default long totalVirtualBytes() {
        return getMemory().totalVirtual();
    }

    /** Virtual memory currently in use in bytes. */
    default long virtualInUseBytes() {
        return getMemory().virtualInUse();
    }

    // ── Swap ──────────────────────────────────────────────────────────────────

    /** Total swap/page file size in bytes. */
    default long swapTotalBytes() {
        return getMemory().swapTotal();
    }

    /** Swap currently in use in bytes. */
    default long swapUsedBytes() {
        return getMemory().swapUsed();
    }

    /** True if any swap is currently being used. */
    default boolean isSwapping() {
        return getMemory().isSwapping();
    }

    /** Swap usage as a percentage (0–100). */
    default double swapUsagePercent() {
        return getMemory().swapUsagePercent();
    }

    // ── Physical modules ─────────────────────────────────────────────────────

    /** All physical memory modules (DIMMs) installed. */
    default List<MemoryInfo.PhysicalMemoryModule> modules() {
        return getMemory().modules();
    }

    /** Number of memory modules installed. */
    default int moduleCount() {
        return getMemory().moduleCount();
    }

    /** All distinct memory types present (e.g. DDR4, DDR5, LPDDR5). */
    default List<String> memoryTypes() {
        return getMemory().memoryTypes();
    }

    /** All distinct clock speeds present across modules, in Hz. */
    default List<Long> clockSpeeds() {
        return getMemory().clockSpeeds();
    }

    /** True if modules are running at different clock speeds (mismatched kit). */
    default boolean hasMismatchedSpeeds() {
        return getMemory().hasMismatchedSpeeds();
    }

    /** Total capacity of all installed modules in bytes. */
    default long totalModuleCapacity() {
        return getMemory().totalModuleCapacity();
    }

    /** Find a module by its bank label (e.g. "ChannelA-DIMM0"). */
    default Optional<MemoryInfo.PhysicalMemoryModule> findModuleByBank(String bankLabel) {
        return getMemory().findModuleByBank(bankLabel);
    }

    /** Modules from a specific manufacturer. */
    default List<MemoryInfo.PhysicalMemoryModule> modulesByManufacturer(String manufacturer) {
        return getMemory().modulesByManufacturer(manufacturer);
    }

    static MemoryAPI oshi(oshi.SystemInfo systemInfo) {
        return new OshiMemoryAPI(systemInfo);
    }
}