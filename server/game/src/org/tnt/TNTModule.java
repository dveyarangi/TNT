package org.tnt;

import org.tnt.account.IPlayerStore;
import org.tnt.account.PlayerStore;
import org.tnt.multiplayer.Hub;
import org.tnt.multiplayer.HubThread;
import org.tnt.multiplayer.IGameFactory;
import org.tnt.multiplayer.IHub;
import org.tnt.multiplayer.IHubThread;
import org.tnt.multiplayer.IPlayerConnections;
import org.tnt.multiplayer.PlayerConnections;
import org.tnt.multiplayer.network.NettyNetwork;
import org.tnt.multiplayer.network.auth.AuthHandler;
import org.tnt.multiplayer.network.auth.IAuthenticator;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class TNTModule extends AbstractModule
{

	@Override
	protected void configure()
	{
		bind( ITNTServer.class ).           to( TNTServer.class );
		bind( IHub.class ).                 to( Hub.class )                 .in( Singleton.class);
		bind( IHubThread.class ).           to( HubThread.class )           .in( Singleton.class);
		bind( ICalculator.class ).          to( Calculator.class )          .in( Singleton.class);
		bind( IGameFactory.class ).         to( GameFactory.class )         .in( Singleton.class);
		bind( IPlayerStore.class ).         to( PlayerStore.class )         .in( Singleton.class);
		bind( INetworkThread.class )       .to( NettyNetwork.class )        .in( Singleton.class);
		bind( IPlayerConnections.class )   .to( PlayerConnections.class )   .in( Singleton.class);
		bind( IAuthenticator.class )       .to( AuthHandler.class )         .in( Singleton.class);
		bind( IShutdownHook.class )        .to( TNTServer.class )           .in( Singleton.class);
//		bind( IHub.class ).      to( Hub.class );
//		bind( IHub.class ).      to( Hub.class );
//		bind( IHub.class ).      to( Hub.class );
//		bind( IHub.class ).      to( Hub.class );
//		bind( IHub.class ).      to( Hub.class );
	}

}
