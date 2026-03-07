package io.joshuasalcedo.os.application.dto;

import java.util.List;

public record DnsConfigurationDto(
    String hostname,
    String domainName,
    List<String> nameServers,
    List<String> searchDomains
) {}
