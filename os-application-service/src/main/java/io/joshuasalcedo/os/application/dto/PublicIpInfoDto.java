package io.joshuasalcedo.os.application.dto;

public record PublicIpInfoDto(
    String ip,
    String hostname,
    String city,
    String region,
    String country,
    String loc,
    String org,
    String postal,
    String timezone
) {}
