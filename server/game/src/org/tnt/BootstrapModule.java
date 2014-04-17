package org.tnt;

import org.tnt.config.TNTConfig;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class BootstrapModule extends AbstractModule
{

	@Override
	protected void configure()
	{
		bind( TNTServer.class ).            to(TNTServer.class).in( Singleton.class);

		// server shutdown hook
		bind( IShutdownHook.class ).        to( TNTServer.class );

		bind( TNTConfig.class ).            toProvider(TNTConfig.class).in( Singleton.class);

		// multiplayer hub, manages player non-ingame actions
		//		bind( IPl.class ).                 to( Hub.class )                 .in( Singleton.class);
		// hub thread
		//		bind( IHubThread.class ).           to( HubThread.class )           .in( Singleton.class);

	}

}
