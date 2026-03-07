package io.joshuasalcedo.os.application.dto;

public record TcpConnectionDto(
    String localAddress,
    int localPort,
    String remoteAddress,
    int remotePort,
    String state,
    long pid,
    String processName
) {}
