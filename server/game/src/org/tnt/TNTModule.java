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
		// server main class
		bind( ITNTServer.class ).           to( TNTServer.class );
		// server shutdown hook
		bind( IShutdownHook.class )        .to( TNTServer.class )           .in( Singleton.class);
		
		// multiplayer hub, manages player non-ingame actions
		bind( IHub.class ).                 to( Hub.class )                 .in( Singleton.class);
		// hub thread
		bind( IHubThread.class ).           to( HubThread.class )           .in( Singleton.class);
		
		// utility calculator
		bind( ICalculator.class ).          to( Calculator.class )          .in( Singleton.class);
		
		// game resources provider
		bind( IGameFactory.class ).         to( GameFactory.class )         .in( Singleton.class);
		
		// player data store
		bind( IPlayerStore.class ).         to( PlayerStore.class )         .in( Singleton.class);
		
		// server networking thread
		bind( INetworkThread.class )       .to( NettyNetwork.class )        .in( Singleton.class);
		
		// connected players repository
		bind( IPlayerConnections.class )   .to( PlayerConnections.class )   .in( Singleton.class);
		
		// player authentication protocol
		bind( IAuthenticator.class )       .to( AuthHandler.class )         .in( Singleton.class);
//		bind( IHub.class ).      to( Hub.class );
//		bind( IHub.class ).      to( Hub.class );
//		bind( IHub.class ).      to( Hub.class );
//		bind( IHub.class ).      to( Hub.class );
//		bind( IHub.class ).      to( Hub.class );
	}

}
