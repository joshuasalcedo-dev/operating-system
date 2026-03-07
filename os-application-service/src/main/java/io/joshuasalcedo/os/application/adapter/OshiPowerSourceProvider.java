package io.joshuasalcedo.os.application.adapter;

import io.joshuasalcedo.os.domain.Manufacturer;
import io.joshuasalcedo.os.domain.hardware.HardwarePort;
import io.joshuasalcedo.os.domain.hardware.PowerSourceInfo;
import oshi.SystemInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OshiPowerSourceProvider implements HardwarePort.PowerSourceProvider {

    private final SystemInfo systemInfo;

    public OshiPowerSourceProvider(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public List<PowerSourceInfo> provide() {
        return systemInfo.getHardware().getPowerSources().stream()
                .map(ps -> new PowerSourceInfo(
                        ps.getName(),
                        Manufacturer.of(ps.getManufacturer()),
                        ps.getChemistry(),
                        ps.getRemainingCapacityPercent(),
                        ps.getTimeRemainingEstimated(),
                        ps.isCharging(),
                        ps.isDischarging(),
                        ps.isPowerOnLine()
                ))
                .toList();
    }
}
