package io.joshuasalcedo.os.application.adapter;

import io.joshuasalcedo.os.domain.Manufacturer;
import io.joshuasalcedo.os.domain.hardware.HardwarePort;
import io.joshuasalcedo.os.domain.hardware.MemoryInfo;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.VirtualMemory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OshiMemoryProvider implements HardwarePort.MemoryProvider {

    private final SystemInfo systemInfo;

    public OshiMemoryProvider(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public MemoryInfo provide() {
        GlobalMemory mem = systemInfo.getHardware().getMemory();
        VirtualMemory vm = mem.getVirtualMemory();

        List<MemoryInfo.PhysicalMemoryModule> modules = mem.getPhysicalMemory().stream()
                .map(pm -> new MemoryInfo.PhysicalMemoryModule(
                        pm.getBankLabel(),
                        pm.getCapacity(),
                        pm.getClockSpeed(),
                        Manufacturer.of(pm.getManufacturer()),
                        pm.getMemoryType()
                ))
                .toList();

        return new MemoryInfo(
                mem.getTotal(),
                mem.getAvailable(),
                vm.getVirtualMax(),
                vm.getVirtualInUse(),
                vm.getSwapTotal(),
                vm.getSwapUsed(),
                modules
        );
    }
}
