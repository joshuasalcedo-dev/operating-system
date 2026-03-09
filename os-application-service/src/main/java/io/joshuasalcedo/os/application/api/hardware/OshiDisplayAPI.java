package io.joshuasalcedo.os.application.api.hardware;

import io.joshuasalcedo.os.domain.hardware.DisplayInfo;
import oshi.SystemInfo;
import oshi.hardware.Display;

import java.util.List;
import java.util.stream.IntStream;

final class OshiDisplayAPI implements DisplayAPI {

    private final SystemInfo systemInfo;

    OshiDisplayAPI(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public List<DisplayInfo> getDisplays() {
        List<Display> displays = systemInfo.getHardware().getDisplays();
        return IntStream.range(0, displays.size())
                .mapToObj(i -> new DisplayInfo(i, displays.get(i).toString()))
                .toList();
    }
}
