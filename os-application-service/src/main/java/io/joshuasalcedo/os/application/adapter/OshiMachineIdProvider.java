package io.joshuasalcedo.os.application.adapter;

import io.joshuasalcedo.os.domain.DomainPort;
import io.joshuasalcedo.os.domain.MachineId;
import oshi.SystemInfo;
import org.springframework.stereotype.Component;

@Component
public class OshiMachineIdProvider implements DomainPort.MachineIdProvider {

    private final SystemInfo systemInfo;

    public OshiMachineIdProvider(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public MachineId provide() {
        return MachineId.resolve(systemInfo.getHardware().getComputerSystem().getHardwareUUID());
    }
}
