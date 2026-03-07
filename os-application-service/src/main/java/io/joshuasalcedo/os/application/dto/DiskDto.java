package io.joshuasalcedo.os.application.dto;

import java.util.List;

public record DiskDto(
    String name,
    String model,
    String serialNumber,
    String size,
    long reads,
    long writes,
    long readBytes,
    long writeBytes,
    List<String> partitions
) {}
