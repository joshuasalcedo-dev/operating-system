/**
 * Core domain model for operating system introspection.
 *
 * <p>This package contains the root sealed interfaces, identity value objects,
 * and the aggregate {@link io.joshuasalcedo.os.domain.SystemSnapshot}.</p>
 *
 * <h2>Sealed Interfaces</h2>
 * <ul>
 *   <li>{@link io.joshuasalcedo.os.domain.OperatingSystemObject} &mdash;
 *       root marker for all core domain value objects</li>
 *   <li>{@link io.joshuasalcedo.os.domain.DomainPort} &mdash;
 *       sealed port interface defining provider contracts for snapshot and machine ID</li>
 * </ul>
 *
 * @author JoshuaSalcedo
 * @since 1.0.0-SNAPSHOT
 */
package io.joshuasalcedo.os.domain;
