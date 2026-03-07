package io.joshuasalcedo.os.domain.network;

import java.util.List;

/**
 * Sealed port interface for the network package.
 * Defines the provider contracts for network information objects.
 *
 * @author JoshuaSalcedo
 * @since 3/7/2026
 */
public sealed interface NetworkPort permits
	NetworkPort.NetworkSnapshotProvider,
	NetworkPort.TcpConnectionProvider,
	NetworkPort.UdpConnectionProvider,
	NetworkPort.ListeningPortProvider,
	NetworkPort.NetworkRouteProvider,
	NetworkPort.DnsConfigurationProvider,
	NetworkPort.InternetProtocolStatsProvider,
	NetworkPort.PublicIpInfoProvider
	{

	non-sealed interface NetworkSnapshotProvider extends NetworkPort {
		NetworkSnapshot provide();
	}

	non-sealed interface TcpConnectionProvider extends NetworkPort {
		List<TcpConnection> provide();
	}

	non-sealed interface UdpConnectionProvider extends NetworkPort {
		List<UdpConnection> provide();
	}

	non-sealed interface ListeningPortProvider extends NetworkPort {
		List<ListeningPort> provide();
	}

	non-sealed interface NetworkRouteProvider extends NetworkPort {
		List<NetworkRoute> provide();
	}

	non-sealed interface DnsConfigurationProvider extends NetworkPort {
		DnsConfiguration provide();
	}

	non-sealed interface InternetProtocolStatsProvider extends NetworkPort {
		InternetProtocolStats provide();
	}

	non-sealed interface PublicIpInfoProvider extends NetworkPort {
		PublicIpInfo provide();
	}
}
