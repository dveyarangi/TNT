package org.tnt;

import org.tnt.account.IPlayerStore;
import org.tnt.config.TNTConfig;
import org.tnt.debug.Debug;
import org.tnt.multiplayer.IGameFactory;
import org.tnt.multiplayer.IHub;
import org.tnt.multiplayer.network.auth.AuthHandler;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.spinn3r.log5j.Logger;
@Singleton
public class TNTServer implements ITNTServer
{
	private final Logger log = Logger.getLogger(this.getClass());

	@Inject private TNTConfig	config;
	
	@Inject private IGameFactory factory;
	
	@Inject private IPlayerStore store;
	
	@Inject private IHub hub;
	
	@Inject private INetworkThread network; 
	
	@Inject AuthHandler authenticator;
	
	final long startTime = System.currentTimeMillis();
	
	@Override
	public void init()
	{
		log.info( "Starting server..." );
		
		
//		Thread.sleep( 1000 );

		try {
		// loading server configuration:
		config.load();
		
		// loading server resources:
		factory.init();
		
		//calculator.init();
		
		store.init();
		
		hub.init();
		
		network.init();
		
		if(true)
			Debug.init();
		
		}
		catch(Exception e)
		{
			log.fatal( "Failed to start server." );
		}
		
		Runtime.getRuntime().addShutdownHook( new ShutdownHook() );
		log.info( "Server started in " + (System.currentTimeMillis() - startTime) + " ms." );
	}



	public static void main( String[] args ) throws Exception
	{
		Thread.currentThread().setName( "tnt-server" );
		
		Injector injector = Guice.createInjector(new TNTModule());
		
		ITNTServer server = injector.getInstance( ITNTServer.class );
		
		server.init();
		
	/*	TNTConsole console = new TNTConsole( server );
		Thread consoleThread = new Thread( console, "tnt-console" );
		consoleThread.setDaemon( true );
		consoleThread.start();*/
	}

	
	private class ShutdownHook extends Thread
	{
		ShutdownHook() 
		{
			super ("tnt-shutdown");
		}
		@Override
		public void run()
		{
			hub.safeStop();
			network.safeStop();
			log.info( "Started in (uptime " + (System.currentTimeMillis() - startTime) + " ms." );
		}
	}
}
