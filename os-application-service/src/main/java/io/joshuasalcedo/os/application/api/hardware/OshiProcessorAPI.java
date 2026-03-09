package io.joshuasalcedo.os.application.api.hardware;

import io.joshuasalcedo.os.domain.Manufacturer;
import io.joshuasalcedo.os.domain.hardware.ProcessorInfo;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.util.List;

final class OshiProcessorAPI implements ProcessorAPI {

    private final SystemInfo systemInfo;

    OshiProcessorAPI(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public List<ProcessorInfo> getProcessors() {
        CentralProcessor cpu = systemInfo.getHardware().getProcessor();
        CentralProcessor.ProcessorIdentifier id = cpu.getProcessorIdentifier();

        List<ProcessorInfo.CacheInfo> caches = cpu.getProcessorCaches().stream()
                .map(c -> new ProcessorInfo.CacheInfo(
                        c.getLevel(),
                        c.getType().name(),
                        c.getCacheSize(),
                        c.getAssociativity(),
                        c.getLineSize()
                ))
                .toList();

        List<ProcessorInfo.CoreTopology> topology = cpu.getLogicalProcessors().stream()
                .map(lp -> {
                    int efficiency = cpu.getPhysicalProcessors().stream()
                            .filter(pp -> pp.getPhysicalPackageNumber() == lp.getPhysicalPackageNumber()
                                    && pp.getPhysicalProcessorNumber() == lp.getPhysicalProcessorNumber())
                            .findFirst()
                            .map(CentralProcessor.PhysicalProcessor::getEfficiency)
                            .orElse(0);
                    return new ProcessorInfo.CoreTopology(
                            String.valueOf(lp.getProcessorNumber()),
                            String.valueOf(efficiency),
                            lp.getPhysicalProcessorNumber(),
                            lp.getPhysicalPackageNumber(),
                            lp.getNumaNode(),
                            lp.getProcessorGroup()
                    );
                })
                .toList();

        return List.of(new ProcessorInfo(
                id.getName(),
                Manufacturer.of(id.getVendor()),
                id.getIdentifier(),
                id.getProcessorID(),
                id.getMicroarchitecture(),
                cpu.getPhysicalPackageCount(),
                cpu.getPhysicalProcessorCount(),
                cpu.getLogicalProcessorCount(),
                id.getVendorFreq(),
                cpu.getMaxFreq(),
                caches,
                topology
        ));
    }

    @Override
    public List<ProcessorInfo.CacheInfo> getCaches(ProcessorInfo processor, int level) {
        return processor.caches().stream()
                .filter(c -> c.level() == level)
                .toList();
    }
}
