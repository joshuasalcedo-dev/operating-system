package io.joshuasalcedo.os.application.dto;

public record NetworkRouteDto(
    String destination,
    String gateway,
    String netmask,
    String interfaceName,
    int metric
) {}
