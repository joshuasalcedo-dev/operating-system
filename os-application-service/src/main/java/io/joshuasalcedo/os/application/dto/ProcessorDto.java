package io.joshuasalcedo.os.application.dto;

public record ProcessorDto(
    String name,
    String manufacturer,
    String identifier,
    String processorId,
    String microarchitecture,
    int physicalPackages,
    int physicalCores,
    int logicalProcessors,
    String vendorFrequency,
    String maxFrequency
) {}
