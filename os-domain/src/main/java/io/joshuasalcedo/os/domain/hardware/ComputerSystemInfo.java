package io.joshuasalcedo.os.domain.hardware;

import io.joshuasalcedo.os.domain.Manufacturer;
import io.joshuasalcedo.os.domain.SerialNumber;
import io.joshuasalcedo.os.domain.computer.PhysicalComputerId;

import java.util.Objects;

/**
 * Value object representing the overall computer system.
 */
public record ComputerSystemInfo(
        Manufacturer manufacturer,
        String model,
        SerialNumber serialNumber,
        String uuid,
        FirmwareInfo firmware,
        BaseboardInfo baseboard
) implements OSHardwareObject {



    public PhysicalComputerId physicalComputerId(OperatingSystemInfo systemInfo) {
        return PhysicalComputerId.from(this,systemInfo);
    }

    /**
     * Value object for system firmware/BIOS.
     */
    public record FirmwareInfo(
            Manufacturer manufacturer,
            String name,
            String description,
            String version,
            String releaseDate
    ) {
        @Override
        public String toString() {
            return String.format("%s %s v%s (%s)", manufacturer, name, version, releaseDate);
        }
    }

    /**
     * Value object for the baseboard/motherboard.
     */
    public record BaseboardInfo(
            Manufacturer manufacturer,
            String model,
            String version,
            SerialNumber serialNumber
    ) {
        @Override
        public String toString() {
            return String.format("%s %s v%s (S/N: %s)", manufacturer, model, version, serialNumber);
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s (S/N: %s)\n  Firmware: %s\n  Baseboard: %s",
                manufacturer, model, serialNumber, firmware, baseboard);
    }
}