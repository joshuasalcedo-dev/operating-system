package io.joshuasalcedo.os.application.dto;

public record ComputerSystemDto(
    String manufacturer,
    String model,
    String serialNumber,
    String uuid,
    String firmwareManufacturer,
    String firmwareName,
    String firmwareVersion,
    String baseboardManufacturer,
    String baseboardModel,
    String baseboardVersion
) {}
