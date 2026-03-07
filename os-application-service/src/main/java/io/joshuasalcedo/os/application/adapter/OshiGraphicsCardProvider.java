package io.joshuasalcedo.os.application.adapter;

import io.joshuasalcedo.os.domain.Manufacturer;
import io.joshuasalcedo.os.domain.hardware.GraphicsCardInfo;
import io.joshuasalcedo.os.domain.hardware.HardwarePort;
import oshi.SystemInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OshiGraphicsCardProvider implements HardwarePort.GraphicsCardProvider {

    private final SystemInfo systemInfo;

    public OshiGraphicsCardProvider(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public List<GraphicsCardInfo> provide() {
        return systemInfo.getHardware().getGraphicsCards().stream()
                .map(gc -> new GraphicsCardInfo(
                        gc.getName(),
                        gc.getDeviceId(),
                        Manufacturer.of(gc.getVendor()),
                        gc.getVersionInfo(),
                        gc.getVRam()
                ))
                .toList();
    }
}
