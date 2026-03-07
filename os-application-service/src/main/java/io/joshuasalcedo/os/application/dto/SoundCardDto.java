package io.joshuasalcedo.os.application.dto;

public record SoundCardDto(
    String name,
    String codec,
    String driverVersion
) {}
