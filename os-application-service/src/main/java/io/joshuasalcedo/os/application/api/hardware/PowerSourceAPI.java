package io.joshuasalcedo.os.application.api.hardware;

import io.joshuasalcedo.os.domain.hardware.PowerSourceInfo;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for querying power source / battery information.
 */
interface PowerSourceAPI {

    // ── Core ──────────────────────────────────────────────────────────────────

    /** Returns all power sources (batteries, UPS units, AC adapters) detected. */
    List<PowerSourceInfo> getPowerSources();

    // ── Lookup ────────────────────────────────────────────────────────────────

    /** Find a power source by name. */
    default Optional<PowerSourceInfo> findByName(String name) {
        return getPowerSources().stream()
                .filter(p -> p.name().equalsIgnoreCase(name))
                .findFirst();
    }

    // ── Power state ───────────────────────────────────────────────────────────

    /** True if the system is plugged in to AC power (at least one source is on-line). */
    default boolean isOnAcPower() {
        return getPowerSources().stream().anyMatch(PowerSourceInfo::powerOnLine);
    }

    /** True if any power source is currently charging. */
    default boolean isCharging() {
        return getPowerSources().stream().anyMatch(PowerSourceInfo::charging);
    }

    /** True if any power source is currently discharging (running on battery). */
    default boolean isDischarging() {
        return getPowerSources().stream().anyMatch(PowerSourceInfo::discharging);
    }

    /** All sources that are currently charging. */
    default List<PowerSourceInfo> chargingSources() {
        return getPowerSources().stream()
                .filter(PowerSourceInfo::charging)
                .toList();
    }

    /** All sources that are currently discharging. */
    default List<PowerSourceInfo> dischargingSources() {
        return getPowerSources().stream()
                .filter(PowerSourceInfo::discharging)
                .toList();
    }

    // ── Capacity ──────────────────────────────────────────────────────────────

    /**
     * The lowest remaining capacity across all discharging sources (0.0–1.0).
     * Returns 1.0 if no discharging source is found (fully charged / AC only).
     */
    default double lowestRemainingCapacity() {
        return getPowerSources().stream()
                .filter(PowerSourceInfo::discharging)
                .mapToDouble(PowerSourceInfo::remainingCapacityPercent)
                .min()
                .orElse(1.0);
    }

    /** True if any battery is critically low (below 10%). */
    default boolean isCriticallyLow() {
        return getPowerSources().stream().anyMatch(PowerSourceInfo::isCriticallyLow);
    }

    /** True if any battery is low (below 20%). */
    default boolean isLow() {
        return getPowerSources().stream().anyMatch(PowerSourceInfo::isLow);
    }

    /** True if all sources are fully charged (≥ 99%). */
    default boolean isFullyCharged() {
        return getPowerSources().stream().allMatch(PowerSourceInfo::isFullyCharged);
    }

    // ── Time remaining ────────────────────────────────────────────────────────

    /**
     * Estimated time remaining in seconds for the source with the least charge.
     * Returns -1 if still calculating or no discharging source exists.
     */
    default double estimatedTimeRemainingSeconds() {
        return getPowerSources().stream()
                .filter(PowerSourceInfo::discharging)
                .mapToDouble(PowerSourceInfo::timeRemainingEstimated)
                .min()
                .orElse(-1);
    }

    /** True if the time remaining estimate is still being calculated. */
    default boolean isCalculatingTimeRemaining() {
        return getPowerSources().stream().anyMatch(PowerSourceInfo::isCalculatingTimeRemaining);
    }

    // ── Chemistry ─────────────────────────────────────────────────────────────

    /** All distinct battery chemistries present (e.g. "Li-Ion", "NiMH", "LiPolymer"). */
    default List<String> batteryChemistries() {
        return getPowerSources().stream()
                .map(PowerSourceInfo::chemistry)
                .distinct()
                .toList();
    }

    static PowerSourceAPI oshi(oshi.SystemInfo systemInfo) {
        return new OshiPowerSourceAPI(systemInfo);
    }
}