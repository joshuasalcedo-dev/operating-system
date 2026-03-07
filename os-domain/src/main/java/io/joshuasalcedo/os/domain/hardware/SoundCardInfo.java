package io.joshuasalcedo.os.domain.hardware;

import java.util.Objects;

/**
 * Value object representing a sound card.
 */
public record SoundCardInfo(
        String name,
        String codec,
        String driverVersion
) implements OSHardwareObject {

    public SoundCardInfo {
        Objects.requireNonNull(name, "Sound card name must not be null");
    }

    @Override
    public String toString() {
        return String.format("%s (codec=%s, driver=%s)", name, codec, driverVersion);
    }
}