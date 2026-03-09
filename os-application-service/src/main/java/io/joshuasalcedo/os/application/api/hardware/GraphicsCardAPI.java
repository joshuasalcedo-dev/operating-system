package io.joshuasalcedo.os.application.api.hardware;

import io.joshuasalcedo.os.domain.hardware.GraphicsCardInfo;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for querying graphics card information.
 */
interface GraphicsCardAPI {

    // ── Core ──────────────────────────────────────────────────────────────────

    /** Returns all graphics cards detected on the system. */
    List<GraphicsCardInfo> getGraphicsCards();

    // ── Lookup ────────────────────────────────────────────────────────────────

    /** Find a GPU by name (partial, case-insensitive). */
    default Optional<GraphicsCardInfo> findByName(String name) {
        return getGraphicsCards().stream()
                .filter(g -> g.name().toLowerCase().contains(name.toLowerCase()))
                .findFirst();
    }

    /** Find a GPU by device ID. */
    default Optional<GraphicsCardInfo> findByDeviceId(String deviceId) {
        return getGraphicsCards().stream()
                .filter(g -> g.deviceId().equalsIgnoreCase(deviceId))
                .findFirst();
    }

    /** All GPUs from a specific manufacturer (e.g. "NVIDIA", "AMD", "Intel"). */
    default List<GraphicsCardInfo> byManufacturer(String manufacturer) {
        return getGraphicsCards().stream()
                .filter(g -> g.manufacturer() != null &&
                             g.manufacturer().toString().toLowerCase().contains(manufacturer.toLowerCase()))
                .toList();
    }

    // ── VRAM ─────────────────────────────────────────────────────────────────

    /** Total VRAM across all GPUs in bytes. */
    default long totalVRamBytes() {
        return getGraphicsCards().stream()
                .mapToLong(GraphicsCardInfo::vRam)
                .sum();
    }

    /** The GPU with the most VRAM. */
    default Optional<GraphicsCardInfo> mostVRam() {
        return getGraphicsCards().stream()
                .max(java.util.Comparator.comparingLong(GraphicsCardInfo::vRam));
    }

    /** All GPUs with at least the given VRAM in bytes. */
    default List<GraphicsCardInfo> withAtLeastVRam(long bytes) {
        return getGraphicsCards().stream()
                .filter(g -> g.vRam() >= bytes)
                .toList();
    }

    /** All GPUs with at least 4 GB of VRAM (minimum for most modern workloads). */
    default List<GraphicsCardInfo> modernGpus() {
        return withAtLeastVRam(4L * 1_073_741_824L);
    }

    // ── Multi-GPU ─────────────────────────────────────────────────────────────

    /** True if more than one GPU is present. */
    default boolean isMultiGpu() {
        return getGraphicsCards().size() > 1;
    }

    /** True if a dedicated (discrete) GPU is present, detected by ruling out known integrated GPU names. */
    default boolean hasDiscreteGpu() {
        return getGraphicsCards().stream()
                .anyMatch(g -> !g.name().toLowerCase().contains("intel") ||
                               g.vRam() > 512L * 1_048_576L);
    }

    /** True if an integrated GPU is present (Intel UHD / AMD Radeon integrated patterns). */
    default boolean hasIntegratedGpu() {
        return getGraphicsCards().stream()
                .anyMatch(g -> g.name().toLowerCase().contains("uhd") ||
                               g.name().toLowerCase().contains("iris") ||
                               g.name().toLowerCase().contains("radeon graphics"));
    }

    // ── Drivers ───────────────────────────────────────────────────────────────

    /** All distinct driver version strings across all GPUs. */
    default List<String> driverVersions() {
        return getGraphicsCards().stream()
                .map(GraphicsCardInfo::versionInfo)
                .distinct()
                .toList();
    }

}