package io.joshuasalcedo.os.domain.network;

/**
 * Sealed interface for all network-related operating system value objects.
 *
 * @author JoshuaSalcedo
 * @since 3/7/2026
 */
public sealed interface OSNetworkObject permits
	ConnectionState,
	DnsConfiguration,
	InternetProtocolStats,
	ListeningPort,
	NetworkRoute,
	NetworkSnapshot,
	PublicIpInfo,
	SocketAddress,
	TcpConnection,
	TransportProtocol,
	UdpConnection
	{
}
