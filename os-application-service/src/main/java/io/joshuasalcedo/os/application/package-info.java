/**
 * Operating System Application Service module.
 *
 * <p>This module implements the hexagonal (ports and adapters) architecture pattern
 * for operating system hardware and network introspection.</p>
 *
 * <h2>Package Structure</h2>
 * <ul>
 *   <li>{@code adapter} &mdash; OSHI-backed implementations of domain port interfaces.
 *       Each adapter is a Spring {@code @Component} that reads live hardware data
 *       via the <a href="https://github.com/oshi/oshi">OSHI</a> library and maps it
 *       to domain value objects.</li>
 *   <li>{@code dto} &mdash; Flat, serializable Data Transfer Objects for API responses.
 *       DTOs use only primitive and standard JDK types (String, long, Instant, etc.)
 *       to remain transport-agnostic.</li>
 *   <li>{@code service} &mdash; Application services that coordinate port providers
 *       and return DTOs. These are the primary entry points for consumers of this module.</li>
 * </ul>
 *
 * <h2>Spring Integration</h2>
 * <p>All adapters are auto-discovered via component scanning. A shared
 * {@link oshi.SystemInfo} bean is provided by
 * {@link io.joshuasalcedo.os.application.adapter.OshiConfiguration}.</p>
 *
 * @author JoshuaSalcedo
 * @since 1.0.0-SNAPSHOT
 * @see io.joshuasalcedo.os.domain
 */
package io.joshuasalcedo.os.application;
