package io.joshuasalcedo.os.application.dto;

public record PowerSourceDto(
    String name,
    String manufacturer,
    String chemistry,
    String remainingCapacity,
    String timeRemaining,
    boolean charging,
    boolean powerOnLine
) {}
