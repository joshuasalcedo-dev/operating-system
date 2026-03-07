
package io.joshuasalcedo.os.domain.hardware;

import io.joshuasalcedo.os.domain.Manufacturer;

import java.util.Locale;
import java.util.Objects;

/**
 * Value object representing a graphics card.
 */
public record GraphicsCardInfo(
        String name,
        String deviceId,
        Manufacturer manufacturer,
        String versionInfo,
        long vRam
) implements OSHardwareObject {

    public GraphicsCardInfo {
        Objects.requireNonNull(name, "Graphics card name must not be null");
    }

    public String vRamFormatted() {
        if (vRam >= 1_073_741_824L) {
            return String.format(Locale.ROOT, "%.0f GB", vRam / 1_073_741_824.0);
        } else if (vRam >= 1_048_576L) {
            return String.format(Locale.ROOT, "%.0f MB", vRam / 1_048_576.0);
        }
        return vRam + " bytes";
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s VRAM)", manufacturer, name, vRamFormatted());
    }
}