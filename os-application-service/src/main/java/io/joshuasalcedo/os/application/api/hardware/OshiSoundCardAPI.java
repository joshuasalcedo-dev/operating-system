package io.joshuasalcedo.os.application.api.hardware;

import io.joshuasalcedo.os.domain.hardware.SoundCardInfo;
import oshi.SystemInfo;

import java.util.List;

final class OshiSoundCardAPI implements SoundCardAPI {

    private final SystemInfo systemInfo;

    OshiSoundCardAPI(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public List<SoundCardInfo> getSoundCards() {
        return systemInfo.getHardware().getSoundCards().stream()
                .map(sc -> new SoundCardInfo(
                        sc.getName(),
                        sc.getCodec(),
                        sc.getDriverVersion()
                ))
                .toList();
    }
}
