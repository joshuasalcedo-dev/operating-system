package io.joshuasalcedo.os.application.dto;

import java.time.Instant;

public record OperatingSystemInfoDto(
    String family,
    String manufacturer,
    String versionInfo,
    String buildNumber,
    int bitness,
    int processCount,
    int threadCount,
    Instant bootTime,
    String uptime,
    boolean elevated
) {}
