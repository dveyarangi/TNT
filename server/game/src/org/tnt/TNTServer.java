package org.tnt;

import org.tnt.account.IPlayerStore;
import org.tnt.bootstrap.BootstrapModule;
import org.tnt.bootstrap.IShutdownHook;
import org.tnt.config.TNTConfig;
import org.tnt.debug.Debug;
import org.tnt.game.GameModule;
import org.tnt.game.IGameFactory;
import org.tnt.network.INetworkThread;
import org.tnt.network.NetworkModule;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.util.Modules;
import com.spinn3r.log5j.Logger;

@Singleton
public class TNTServer extends Thread implements ITNTServer, IShutdownHook
{
	private final Logger log = Logger.getLogger(this.getClass());

	@Inject private TNTConfig	config;

	@Inject private IHalls halls;

	@Inject private IGameFactory factory;

	@Inject private IPlayerStore store;

	@Inject private INetworkThread network;

	@Inject IShutdownHook shutdownHook;

	final long startTime = System.currentTimeMillis();

	public TNTServer()
	{
		super( "tnt-server" );
	}

	@Override
	public void init()
	{
		log.info( "Starting server..." );


		//		Thread.sleep( 1000 );

		try {
			// loading server configuration:
			config.load();

			halls.init();

			// loading server resources:
			factory.init();

			//calculator.init();

			store.init();

			network.init();

			if(true) {
				Debug.init();
			}

		}
		catch(Exception e)
		{
			log.fatal( "Failed to start server." );
			fail(e);
		}

		Runtime.getRuntime().addShutdownHook( (Thread)shutdownHook );
		log.info( "Server started in " + (System.currentTimeMillis() - startTime) + " ms." );
	}



	public static void main( final String[] args ) throws Exception
	{
		Thread.currentThread().setName( "tnt-server" );

		Module serverModule = Modules.combine(
				new BootstrapModule(),
				new GameModule(),
				new NetworkModule()
				);

		Injector injector = Guice.createInjector( serverModule );

		ITNTServer server = injector.getInstance( ITNTServer.class );
		//		injector.createChildInjector( arg0 )
		server.init();

		/*	TNTConsole console = new TNTConsole( server );
		Thread consoleThread = new Thread( console, "tnt-console" );
		consoleThread.setDaemon( true );
		consoleThread.start();*/
	}


	@Override
	public void run()
	{
		log.info( "Shutting down server..." );
		network.safeStop();
		log.info( "Server decomposed (uptime " + ((System.currentTimeMillis() - startTime)/1000/60) + " min.)" );
	}
	@Override
	public void fail(final Exception e)
	{
		log.fatal( "Server crushed.", e );
		this.start();
		System.exit( 1 );
	}
	@Override
	public void shutdown()
	{
		this.start();
		System.exit( 1 );
	}

}
