package io.joshuasalcedo.os.application.api.hardware;

import io.joshuasalcedo.os.domain.Manufacturer;
import io.joshuasalcedo.os.domain.hardware.PowerSourceInfo;
import oshi.SystemInfo;

import java.util.List;

final class OshiPowerSourceAPI implements PowerSourceAPI {

    private final SystemInfo systemInfo;

    OshiPowerSourceAPI(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public List<PowerSourceInfo> getPowerSources() {
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
