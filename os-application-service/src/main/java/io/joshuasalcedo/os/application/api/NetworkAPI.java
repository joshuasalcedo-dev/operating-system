package io.joshuasalcedo.os.application.api;

import io.joshuasalcedo.os.domain.network.*;

import java.util.List;

/**
 * NetworkAPI class.
 *
 * @author JoshuaSalcedo
 * @since 3/9/2026 7:38 AM
 */

public interface NetworkAPI {

	DnsConfiguration getDnsConfiguration();
	InternetProtocolStats getInternetProtocolStats();
	List<ListeningPort> getPorts();
	List<NetworkRoute> getRoutes();

	List<TcpConnection> getConnections();

	/** Returns all TCP connections in the given state. */
	List<TcpConnection> getConnections(ConnectionState state);

	List<UdpConnection> getEndpoints();

	PublicIpInfo getPublicIpInfo();

//	void stopPort(int port);
//	void stopProcess(long pid);
}
