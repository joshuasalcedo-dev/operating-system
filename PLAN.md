# Plan: os-client Auto-Configuration (Local Agent)

## Context
The `os-client` module is a Spring Boot auto-configuration starter that runs **on the machine** as a local agent. When you add it as a dependency, it auto-configures OSHI providers, takes hardware/network snapshots on a schedule, saves them to disk, and optionally pushes them to a remote server. The current `ServerProperty` and `ServerAutoConfiguration` are incomplete stubs.

## Properties Structure

```yaml
os:
  enabled: true                        # master kill-switch
  snapshot:
    enabled: true                      # enable scheduled snapshots
    cron: "0 */5 * * * *"              # cron expression (default: every 5 min)
    path: ./snapshots/                 # where to save JSON snapshots
    retain: 10                         # max snapshots to keep on disk
  hardware:
    enabled: true                      # auto-configure OSHI hardware providers
  network:
    enabled: true                      # auto-configure network providers
    public-ip: true                    # resolve public IP via ipinfo.io
  server:
    enabled: false                     # push snapshots to a remote endpoint
    url: https://api.example.com/snapshots
    connect-timeout: 5000
    read-timeout: 10000
```

## Files to Create/Modify

### 1. Properties — `OsProperties.java`
**Path:** `os-client/src/main/java/io/joshuasalcedo/os/client/config/OsProperties.java`

Replace `ServerProperty` with a properly structured `@ConfigurationProperties(prefix = "os")`:
- `enabled` (boolean, default true)
- `SnapshotProperties` nested: `enabled`, `cron`, `path`, `retain`
- `HardwareProperties` nested: `enabled`
- `NetworkProperties` nested: `enabled`, `publicIp`
- `ServerProperties` nested: `enabled`, `url`, `connectTimeout`, `readTimeout`

All with getters/setters (or Lombok `@Data`).

### 2. Auto-Configuration — `OsAutoConfiguration.java`
**Path:** `os-client/src/main/java/io/joshuasalcedo/os/client/config/OsAutoConfiguration.java`

Master `@AutoConfiguration`:
- `@ConditionalOnProperty(prefix = "os", name = "enabled", havingValue = "true", matchIfMissing = true)`
- Imports: `HardwareAutoConfiguration`, `NetworkAutoConfiguration`, `SnapshotAutoConfiguration`, `ServerAutoConfiguration`

### 3. Hardware Config — `HardwareAutoConfiguration.java`
**Path:** `os-client/src/main/java/io/joshuasalcedo/os/client/config/HardwareAutoConfiguration.java`

- `@ConditionalOnProperty(prefix = "os.hardware", name = "enabled", havingValue = "true", matchIfMissing = true)`
- Creates `SystemInfo` bean (`@ConditionalOnMissingBean`)
- Creates all `Oshi*Provider` beans (`@ConditionalOnMissingBean` each)
- Creates `SystemSnapshotService` bean
- Creates `MachineConfiguration` bean (with `Supplier<MachineId>` delegating to `DomainPort.MachineIdProvider::provide`)

### 4a. OSHI Network Adapter — `OshiNetworkSnapshotProvider.java` (NEW)
**Path:** `os-application-service/src/main/java/io/joshuasalcedo/os/application/adapter/OshiNetworkSnapshotProvider.java`

- Implements `NetworkPort.NetworkSnapshotProvider`
- Uses OSHI `SystemInfo` to gather TCP connections, UDP endpoints, listening ports, routes, DNS config, and IP protocol stats
- Maps OSHI types to domain records

### 4b. DtoMapper additions (MODIFY)
**Path:** `os-application-service/src/main/java/io/joshuasalcedo/os/application/service/DtoMapper.java`

Add static `toDto()` methods for: `NetworkSnapshot`, `TcpConnection`, `UdpConnection`, `ListeningPort`, `NetworkRoute`, `DnsConfiguration`, `PublicIpInfo`

### 4c. NetworkSnapshotService (NEW)
**Path:** `os-application-service/src/main/java/io/joshuasalcedo/os/application/service/NetworkSnapshotService.java`

- Constructor injects `NetworkPort.NetworkSnapshotProvider` and optionally `NetworkPort.PublicIpInfoProvider`
- `getNetworkSnapshot()` -> `NetworkSnapshotDto`
- `getPublicIpInfo()` -> `PublicIpInfoDto` (returns null if provider absent)

### 4d. Network Config — `NetworkAutoConfiguration.java`
**Path:** `os-client/src/main/java/io/joshuasalcedo/os/client/config/NetworkAutoConfiguration.java`

- `@ConditionalOnProperty(prefix = "os.network", name = "enabled", havingValue = "true", matchIfMissing = true)`
- Creates `OshiNetworkSnapshotProvider` bean (`@ConditionalOnMissingBean`)
- Creates `NetworkSnapshotService` bean

### 5. Snapshot Config — `SnapshotAutoConfiguration.java`
**Path:** `os-client/src/main/java/io/joshuasalcedo/os/client/config/SnapshotAutoConfiguration.java`

- `@ConditionalOnProperty(prefix = "os.snapshot", name = "enabled", havingValue = "true")`
- `@EnableScheduling`
- Creates `SnapshotScheduler` bean

### 6. Server Config — `ServerAutoConfiguration.java`
**Path:** `os-client/src/main/java/io/joshuasalcedo/os/client/config/ServerAutoConfiguration.java`

- `@ConditionalOnProperty(prefix = "os.server", name = "enabled", havingValue = "true")`
- Creates `RestClient` bean with `url`, `connectTimeout`, `readTimeout`
- Creates `SnapshotPublisher` bean

### 7. Snapshot Scheduler — `SnapshotScheduler.java`
**Path:** `os-client/src/main/java/io/joshuasalcedo/os/client/snapshot/SnapshotScheduler.java`

- `@Scheduled(cron = "${os.snapshot.cron:0 */5 * * * *}")`
- Injects `DomainPort.SystemSnapshotProvider`
- Takes snapshot -> writes JSON to `os.snapshot.path`
- Prunes old files (keeps `os.snapshot.retain`)
- If `SnapshotPublisher` bean exists, also pushes to remote

### 8. Snapshot Publisher — `SnapshotPublisher.java`
**Path:** `os-client/src/main/java/io/joshuasalcedo/os/client/snapshot/SnapshotPublisher.java`

- POSTs `SystemSnapshotDto` via `RestClient` to `os.server.url`

### 9. AutoConfiguration.imports
**Path:** `os-client/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

Register `io.joshuasalcedo.os.client.config.OsAutoConfiguration`

### 10. Remove Spring from os-application-service
- Remove `spring-context` dependency from `os-application-service/pom.xml`
- Strip `@Component` from all 12 Oshi*Provider classes
- Strip `@Service` from `SystemSnapshotService`
- Strip `@Configuration`/`@Bean` from `MachineConfiguration` — make it a plain class with a public method
- Delete `OshiConfiguration.java` (SystemInfo bean moves to `HardwareAutoConfiguration`)
- All classes become plain POJOs; os-client auto-config is solely responsible for wiring

### 11. MachineConfiguration — implement `machineIdSupplier`
**Path:** `os-application-service/src/main/java/io/joshuasalcedo/os/application/config/MachineConfiguration.java`

Strip Spring annotations. Keep as plain factory:
```java
public class MachineConfiguration {
    private final DomainPort.MachineIdProvider machineIdProvider;
    public MachineConfiguration(DomainPort.MachineIdProvider machineIdProvider) {
        this.machineIdProvider = machineIdProvider;
    }
    public Supplier<MachineId> machineIdSupplier() {
        return machineIdProvider::provide;
    }
}
```

`HardwareAutoConfiguration` in os-client creates the bean:
```java
@Bean @ConditionalOnMissingBean
public MachineConfiguration machineConfiguration(DomainPort.MachineIdProvider p) {
    return new MachineConfiguration(p);
}
@Bean @ConditionalOnMissingBean
public Supplier<MachineId> machineIdSupplier(MachineConfiguration config) {
    return config.machineIdSupplier();
}
```

### 12. Delete old files
- `os-client/.../server/config/ServerProperty.java`
- `os-client/.../server/config/ServerAutoConfiguration.java`
- `os-client/.../hardware/HardwareClient.java`
- `os-application-service/.../adapter/OshiConfiguration.java`

## Package Structure (final)

```
os-application-service/
  io.joshuasalcedo.os.application
  ├── adapter/
  │   ├── (existing Oshi*Provider classes)
  │   └── OshiNetworkSnapshotProvider.java   # NEW
  ├── config/
  │   └── MachineConfiguration.java          # MODIFIED
  └── service/
      ├── DtoMapper.java                     # MODIFIED (network toDto methods)
      ├── SystemSnapshotService.java         # existing
      └── NetworkSnapshotService.java        # NEW

os-client/
  io.joshuasalcedo.os.client
  ├── config/
  │   ├── OsProperties.java
  │   ├── OsAutoConfiguration.java
  │   ├── HardwareAutoConfiguration.java
  │   ├── NetworkAutoConfiguration.java
  │   ├── SnapshotAutoConfiguration.java
  │   └── ServerAutoConfiguration.java
  └── snapshot/
      ├── SnapshotScheduler.java
      └── SnapshotPublisher.java
```

## Implementation Notes

- Use `@Bean` + `@ConditionalOnMissingBean` for every provider (not `@ComponentScan`)
- Existing `@Component` adapters won't conflict — `@ConditionalOnMissingBean` defers to them
- No `PublicIpInfoProvider` implementation yet — NetworkAutoConfiguration skips it for now
- `DtoMapper` is static utility — no bean needed
- `MachineConfiguration.machineIdSupplier` delegates to `DomainPort.MachineIdProvider::provide` (less duplication)

## Verification

1. `mvn compile -pl operating-system/os-client -am` — must compile
2. Add `os-client` as dependency in a test Spring Boot app
3. Set `os.enabled=true`, `os.snapshot.enabled=true` in `application.yml`
4. App should auto-configure all providers and start taking snapshots on schedule
5. With `os.server.enabled=true` + `os.server.url=...`, snapshots should POST to remote
