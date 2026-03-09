package io.joshuasalcedo.os.application.api.hardware;

import io.joshuasalcedo.os.domain.Manufacturer;
import io.joshuasalcedo.os.domain.hardware.MemoryInfo;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.VirtualMemory;

import java.util.List;

final class OshiMemoryAPI implements MemoryAPI {

    private final SystemInfo systemInfo;

    OshiMemoryAPI(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public MemoryInfo getMemory() {
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
