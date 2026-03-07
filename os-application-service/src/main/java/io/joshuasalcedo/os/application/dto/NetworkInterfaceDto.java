package io.joshuasalcedo.os.application.dto;

import java.util.List;

public record NetworkInterfaceDto(
    String name,
    String displayName,
    String macAddress,
    String speed,
    List<String> ipv4Addresses,
    List<String> ipv6Addresses,
    long bytesReceived,
    long bytesSent
) {}
