# Operating System Introspection Library

A cross-platform hardware and network introspection library built on [OSHI](https://github.com/oshi/oshi). Follows hexagonal architecture principles with sealed interfaces to enforce strict domain boundaries.

---

## Module Structure

| Module | Artifact ID | Description |
|--------|-------------|-------------|
| `os-domain` | `os-domain` | Pure domain model — sealed interfaces, value objects, port definitions. Zero runtime dependencies. |
| `os-application-service` | `os-application-service` | Spring adapters, OSHI-backed port implementations, and DTOs for the API layer. |

---

## Repository Setup

Add one of the following repository configurations to your `pom.xml` or `settings.xml`.

### Nexus

```xml
<repositories>
    <repository>
        <id>joshuasalcedo-releases</id>
        <url>https://nexus.joshuasalcedo.io/repository/maven-releases/</url>
    </repository>
    <repository>
        <id>joshuasalcedo-snapshots</id>
        <url>https://nexus.joshuasalcedo.io/repository/maven-snapshots/</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

### GitHub Packages

```xml
<repositories>
    <repository>
        <id>github-operating-system</id>
        <url>https://maven.pkg.github.com/joshuasalcedo-dev/operating-system</url>
    </repository>
</repositories>
```

> **Note:** GitHub Packages requires authentication. Add a `<server>` entry in your `~/.m2/settings.xml` with your GitHub username and a personal access token that has `read:packages` scope.

---

## Dependency

### Domain only

Use this when you only need the domain types (sealed interfaces, value objects, port definitions) with no transitive dependencies.

```xml
<dependency>
    <groupId>io.joshuasalcedo.os</groupId>
    <artifactId>os-domain</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Full application service (Spring Boot)

Use this when you want the complete adapter layer with OSHI integration and Spring auto-configuration.

```xml
<dependency>
    <groupId>io.joshuasalcedo.os</groupId>
    <artifactId>os-application-service</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

---

## Quick Start

```java
@Autowired
private SystemSnapshotService systemSnapshotService;

SystemSnapshot snapshot = systemSnapshotService.capture();
System.out.println(snapshot);
```

---

## Architecture

### Sealed Interfaces

The domain model is organized around sealed interface hierarchies scoped per package:

- **`OperatingSystemObject`** — OS-level introspection types (version, build, processes).
- **`OSHardwareObject`** — Hardware introspection types (processor, memory, disks, graphics, USB, sound, power).
- **`OSNetworkObject`** — Network introspection types (interfaces, connections, protocols).

Each sealed interface restricts which records and sub-interfaces may implement it, providing compile-time guarantees on the domain model.

### Ports

Ports are defined as sealed interfaces in `os-domain`, each with non-sealed sub-interfaces for specific use cases:

- **`DomainPort`** — Entry point for OS-level queries.
- **`HardwarePort`** — Entry point for hardware queries.
- **`NetworkPort`** — Entry point for network queries.

Sub-interfaces remain non-sealed so that adapters in `os-application-service` can implement them freely.

### Adapters

Adapters in `os-application-service` implement the port interfaces using OSHI as the underlying provider. They are Spring-managed beans, auto-configured and ready for injection.

### DTOs

A dedicated DTO layer in `os-application-service` maps domain objects to API-friendly representations, keeping the domain model decoupled from serialization concerns.

---

## Deploy

### To Nexus

```bash
mvn deploy -DskipTests
```

### To GitHub Packages

```bash
mvn deploy -Pgithub-deploy -DskipTests
```

### Javadoc to Nexus

```bash
mvn javadoc:javadoc site:deploy
```

### Javadoc to GitHub Pages

```bash
mvn site:stage scm-publish:publish-scm -Pgithub-deploy
```

### All at once

```bash
groovy deploy.groovy
```

---

## Javadoc

- **Nexus:** [https://nexus.joshuasalcedo.io/repository/docs/io.joshuasalcedo.os/operating-system/1.0.0-SNAPSHOT/](https://nexus.joshuasalcedo.io/repository/docs/io.joshuasalcedo.os/operating-system/1.0.0-SNAPSHOT/)
- **GitHub Pages:** [https://joshuasalcedo-dev.github.io/operating-system/](https://joshuasalcedo-dev.github.io/operating-system/)
