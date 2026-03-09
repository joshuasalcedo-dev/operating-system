package io.joshuasalcedo.os.application.api.hardware;

import io.joshuasalcedo.os.domain.Manufacturer;
import io.joshuasalcedo.os.domain.SerialNumber;
import io.joshuasalcedo.os.domain.hardware.ComputerSystemInfo;
import oshi.SystemInfo;
import oshi.hardware.Baseboard;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Firmware;

import java.util.Optional;

/**
 * Service for reading the overall computer system identity —
 * manufacturer, model, serial number, UUID, firmware, and baseboard.
 */
interface ComputerSystemAPI {

    // ── Core ──────────────────────────────────────────────────────────────────

    ComputerSystemInfo getComputerSystem();

    // ── Convenience ───────────────────────────────────────────────────────────

    /** The system manufacturer (e.g. "MSI", "Dell Inc."). */
    default Manufacturer manufacturer() {
        return getComputerSystem().manufacturer();
    }

    /** The system model string (e.g. "MS-7D95"). */
    default String model() {
        return getComputerSystem().model();
    }

    /** The system serial number. May be "Unknown" on consumer boards. */
    default SerialNumber serialNumber() {
        return getComputerSystem().serialNumber();
    }

    /** The hardware UUID — best source for a stable machine identity. */
    default String uuid() {
        return getComputerSystem().uuid();
    }

    /** Firmware/BIOS information. */
    default Optional<ComputerSystemInfo.FirmwareInfo> firmware() {
        return Optional.ofNullable(getComputerSystem().firmware());
    }

    /** Baseboard/motherboard information. */
    default Optional<ComputerSystemInfo.BaseboardInfo> baseboard() {
        return Optional.ofNullable(getComputerSystem().baseboard());
    }

    // ── UUID quality checks ───────────────────────────────────────────────────

    /**
     * True if the UUID looks like a real hardware UUID (not a placeholder).
     * Cheap boards often return "03000200-0400-0500-0006-000700080009".
     */
    default boolean hasRealUuid() {
        String u = uuid();
        return u != null
                && !u.isBlank()
                && !u.equalsIgnoreCase("Unknown")
                && !u.startsWith("03000200");
    }

    /** True if the serial number is a real value (not "Unknown" or "Default string"). */
    default boolean hasRealSerialNumber() {
        String s = serialNumber() != null ? serialNumber().value() : null;
        return s != null
                && !s.isBlank()
                && !s.equalsIgnoreCase("Unknown")
                && !s.equalsIgnoreCase("Default string")
                && !s.equalsIgnoreCase("To be filled by O.E.M.");
    }

    /**
     * Best available stable machine identifier — prefers real UUID,
     * falls back to serial number, then null.
     */
    default Optional<String> bestMachineId() {
        if (hasRealUuid())           return Optional.of(uuid());
        if (hasRealSerialNumber())   return Optional.of(serialNumber().value());
        return Optional.empty();
    }

    // ── Factory ───────────────────────────────────────────────────────────────

    static ComputerSystemAPI createDefault() {
        return new OshiComputerSystemAPI(new SystemInfo());
    }

    static ComputerSystemAPI createDefault(SystemInfo systemInfo) {
        return new OshiComputerSystemAPI(systemInfo);
    }
}

// ── OSHI Implementation ───────────────────────────────────────────────────────

/**
 * Package-private OSHI-backed implementation of {@link ComputerSystemAPI}.
 */
class OshiComputerSystemAPI implements ComputerSystemAPI {

    private final SystemInfo systemInfo;

    OshiComputerSystemAPI(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public ComputerSystemInfo getComputerSystem() {
        ComputerSystem cs = systemInfo.getHardware().getComputerSystem();
        Firmware       fw = cs.getFirmware();
        Baseboard      bb = cs.getBaseboard();

        return new ComputerSystemInfo(
                Manufacturer.of(cs.getManufacturer()),
                cs.getModel(),
                SerialNumber.of(cs.getSerialNumber()),
                cs.getHardwareUUID(),
                new ComputerSystemInfo.FirmwareInfo(
                        Manufacturer.of(fw.getManufacturer()),
                        fw.getName(),
                        fw.getDescription(),
                        fw.getVersion(),
                        fw.getReleaseDate()
                ),
                new ComputerSystemInfo.BaseboardInfo(
                        Manufacturer.of(bb.getManufacturer()),
                        bb.getModel(),
                        bb.getVersion(),
                        SerialNumber.of(bb.getSerialNumber())
                )
        );
    }
}