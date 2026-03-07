package io.joshuasalcedo.os.application.dto;

public record GraphicsCardDto(
    String name,
    String deviceId,
    String manufacturer,
    String versionInfo,
    String vRam
) {}
