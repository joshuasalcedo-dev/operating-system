package io.joshuasalcedo.os.domain.hardware;

/**
 * Value object representing a display/monitor.
 */
public record DisplayInfo(
        int index,
        String edid
) implements OSHardwareObject {

    /** True if this display has EDID data available. */
    public boolean hasEdid() {
        return edid != null && !edid.isBlank();
    }

    @Override
    public String toString() {
        return String.format("Display %d", index);
    }
}