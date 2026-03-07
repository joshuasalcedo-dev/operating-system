package io.joshuasalcedo.os.application.adapter;

import io.joshuasalcedo.os.domain.Manufacturer;
import io.joshuasalcedo.os.domain.SerialNumber;
import io.joshuasalcedo.os.domain.hardware.ComputerSystemInfo;
import io.joshuasalcedo.os.domain.hardware.HardwarePort;
import oshi.SystemInfo;
import oshi.hardware.Baseboard;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Firmware;
import org.springframework.stereotype.Component;

@Component
public class OshiComputerSystemProvider implements HardwarePort.ComputerSystemProvider {

    private final SystemInfo systemInfo;

    public OshiComputerSystemProvider(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public ComputerSystemInfo provide() {
        ComputerSystem cs = systemInfo.getHardware().getComputerSystem();
        Firmware fw = cs.getFirmware();
        Baseboard bb = cs.getBaseboard();
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
