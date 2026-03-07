package io.joshuasalcedo.os.application.dto;

public record UdpConnectionDto(
    String localAddress,
    int localPort,
    long pid,
    String processName
) {}
