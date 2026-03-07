package io.joshuasalcedo.os.application.adapter;

import io.joshuasalcedo.os.domain.Manufacturer;
import io.joshuasalcedo.os.domain.hardware.HardwarePort;
import io.joshuasalcedo.os.domain.hardware.OperatingSystemInfo;
import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OshiOperatingSystemInfoProvider implements HardwarePort.OperatingSystemInfoProvider {

    private final SystemInfo systemInfo;

    public OshiOperatingSystemInfoProvider(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public OperatingSystemInfo provide() {
        OperatingSystem os = systemInfo.getOperatingSystem();
        OperatingSystem.OSVersionInfo ver = os.getVersionInfo();
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
                os.isElevated()
        );
    }
}
