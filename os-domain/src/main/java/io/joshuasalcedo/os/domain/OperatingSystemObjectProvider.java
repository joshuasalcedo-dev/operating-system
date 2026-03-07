package io.joshuasalcedo.os.domain;

/**
 * OperatingSystemObjectProvider class.
 * @author JoshuaSalcedo
 * @since 3/7/2026 7:17 PM
 */
 
public interface OperatingSystemObjectProvider<T extends OperatingSystemObject> {
		T provide();
}
