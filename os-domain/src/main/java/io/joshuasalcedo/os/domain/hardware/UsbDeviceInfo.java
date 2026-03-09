
package io.joshuasalcedo.os.domain.hardware;

import io.joshuasalcedo.os.domain.Manufacturer;
import io.joshuasalcedo.os.domain.SerialNumber;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Value object representing a USB device.
 */
public record UsbDeviceInfo(
        String name,
        Manufacturer manufacturer,
        SerialNumber serialNumber,
        String vendorId,
        String productId,
        String uniqueDeviceId,
        List<UsbDeviceInfo> connectedDevices
) implements OSHardwareObject {

    public UsbDeviceInfo {
        Objects.requireNonNull(name, "USB device name must not be null");
        connectedDevices = connectedDevices != null ? Collections.unmodifiableList(connectedDevices) : List.of();
    }

    /** True if this device has child devices connected (i.e. it's a hub). */
    public boolean isHub() {
        return !connectedDevices.isEmpty();
    }

    /** True if this is a leaf device with no children (actual peripheral). */
    public boolean isLeafDevice() {
        return connectedDevices.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("%s (%s) [%s:%s]", name, manufacturer, vendorId, productId);
    }
}