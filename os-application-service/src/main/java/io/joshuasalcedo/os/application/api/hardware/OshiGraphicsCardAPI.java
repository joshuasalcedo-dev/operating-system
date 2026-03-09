package io.joshuasalcedo.os.application.api.hardware;

import io.joshuasalcedo.os.domain.Manufacturer;
import io.joshuasalcedo.os.domain.hardware.GraphicsCardInfo;
import oshi.SystemInfo;

import java.util.List;

final class OshiGraphicsCardAPI implements GraphicsCardAPI {

    private final SystemInfo systemInfo;

    OshiGraphicsCardAPI(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public List<GraphicsCardInfo> getGraphicsCards() {
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
