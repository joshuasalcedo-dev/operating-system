package io.joshuasalcedo.os.application.api.hardware;

import io.joshuasalcedo.os.domain.hardware.DisplayInfo;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for querying display / monitor information.
 * <p>
 * {@link DisplayInfo} currently carries raw EDID bytes. Richer resolution/refresh
 * data lives in the EDID blob — these helpers operate on what is available in the
 * value object itself and leave EDID parsing to a higher-level layer.
 */
interface DisplayAPI {

    // ── Core ──────────────────────────────────────────────────────────────────

    /** Returns all displays/monitors detected on the system. */
    List<DisplayInfo> getDisplays();

    // ── Count ─────────────────────────────────────────────────────────────────

    /** Number of displays currently connected. */
    default int count() {
        return getDisplays().size();
    }

    /** True if more than one display is connected. */
    default boolean isMultiMonitor() {
        return count() > 1;
    }

    /** True if no display is connected (headless / server). */
    default boolean isHeadless() {
        return count() == 0;
    }

    // ── Lookup ────────────────────────────────────────────────────────────────

    /** Find a display by its zero-based index. */
    default Optional<DisplayInfo> findByIndex(int index) {
        return getDisplays().stream()
                .filter(d -> d.index() == index)
                .findFirst();
    }

    /** The primary display (index 0). */
    default Optional<DisplayInfo> primaryDisplay() {
        return findByIndex(0);
    }

    // ── EDID ──────────────────────────────────────────────────────────────────

    /** All displays that have a non-null/non-empty EDID string. */
    default List<DisplayInfo> displaysWithEdid() {
        return getDisplays().stream()
                .filter(DisplayInfo::hasEdid)
                .toList();
    }

    /** True if the display at the given index has EDID data available. */
    default boolean hasEdid(int index) {
        return findByIndex(index)
                .map(DisplayInfo::hasEdid)
                .orElse(false);
    }

}