package io.joshuasalcedo.os.domain.hardware;

/**
 * Value object representing a display/monitor.
 */
public record DisplayInfo(
        int index,
        String edid
) implements OSHardwareObject {

    @Override
    public String toString() {
        return String.format("Display %d", index);
    }
}