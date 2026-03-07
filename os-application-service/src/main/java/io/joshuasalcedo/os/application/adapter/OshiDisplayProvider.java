package io.joshuasalcedo.os.application.adapter;

import io.joshuasalcedo.os.domain.hardware.DisplayInfo;
import io.joshuasalcedo.os.domain.hardware.HardwarePort;
import oshi.SystemInfo;
import oshi.hardware.Display;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Component
public class OshiDisplayProvider implements HardwarePort.DisplayProvider {

    private final SystemInfo systemInfo;

    public OshiDisplayProvider(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public List<DisplayInfo> provide() {
        List<Display> displays = systemInfo.getHardware().getDisplays();
        return IntStream.range(0, displays.size())
                .mapToObj(i -> new DisplayInfo(i, displays.get(i).toString()))
                .toList();
    }
}
