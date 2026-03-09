package io.joshuasalcedo.os.application.api.os;

import io.joshuasalcedo.os.domain.Manufacturer;
import io.joshuasalcedo.os.domain.hardware.OperatingSystemInfo;
import oshi.SystemInfo;
import oshi.hardware.ComputerSystem;
import oshi.software.os.OperatingSystem;

import java.time.Instant;

final class OshiOperatingSystemAPI implements OperatingSystemAPI {

    private final SystemInfo systemInfo;

    OshiOperatingSystemAPI(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public OperatingSystemInfo getOperatingSystem() {
        OperatingSystem os = systemInfo.getOperatingSystem();
        OperatingSystem.OSVersionInfo ver = os.getVersionInfo();
        ComputerSystem cs = systemInfo.getHardware().getComputerSystem();

        return new OperatingSystemInfo(
                os.getFamily(),
                Manufacturer.of(os.getManufacturer()),
                ver.toString(),
                ver.getBuildNumber(),
                ver.getCodeName(),
                os.getBitness(),
                os.getProcessCount(),
                os.getThreadCount(),
                Instant.ofEpochSecond(os.getSystemBootTime()),
                os.getSystemUptime(),
                os.isElevated(),
                OperatingSystemAPI.detectEnvironment(
                        os.getFamily(),
                        ver.getBuildNumber(),
                        cs.getManufacturer(),
                        cs.getModel(),
                        cs.getFirmware().getManufacturer(),
                        cs.getBaseboard().getManufacturer()
                )
        );
    }
}
