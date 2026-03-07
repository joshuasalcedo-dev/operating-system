package io.joshuasalcedo.os.domain.hardware;

public enum DeviceStatus implements OSHardwareObject {
    ACTIVE,
    INACTIVE,
    RETIRED,
    SOLD,
    BROKEN,
    IN_STORAGE
}