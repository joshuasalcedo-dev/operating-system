package io.joshuasalcedo.os.application.dto;

public record MemoryDto(
    long totalPhysical,
    long availablePhysical,
    long usedPhysical,
    double usagePercent,
    String totalFormatted,
    String availableFormatted,
    long swapTotal,
    long swapUsed
) {}
