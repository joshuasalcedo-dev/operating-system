package io.joshuasalcedo.os.application.dto;

public record ListeningPortDto(
    String protocol,
    String bindAddress,
    int port,
    long pid,
    String processName
) {}
