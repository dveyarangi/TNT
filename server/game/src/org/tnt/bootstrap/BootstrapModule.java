package org.tnt.bootstrap;

import org.tnt.ITNTServer;
import org.tnt.TNTServer;
import org.tnt.account.IPlayerStore;
import org.tnt.account.PlayerStore;
import org.tnt.config.TNTConfig;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class BootstrapModule extends AbstractModule
{

	@Override
	protected void configure()
	{
		// server main class
		bind( ITNTServer.class ).           to( TNTServer.class );
		// server shutdown hook
		bind( IShutdownHook.class ).        to( TNTServer.class )           .in( Singleton.class);

		bind( TNTConfig.class ).            toProvider(TNTConfig.class).in( Singleton.class);


		// multiplayer hub, manages player non-ingame actions
		//		bind( IHub.class ).                 to( Hub.class )                 .in( Singleton.class);
		// hub thread
		//		bind( IHubThread.class ).           to( HubThread.class )           .in( Singleton.class);

		// player data store
		bind( IPlayerStore.class ).         to( PlayerStore.class )         .in( Singleton.class);
	}

}
