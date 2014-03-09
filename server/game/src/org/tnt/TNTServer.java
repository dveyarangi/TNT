package org.tnt;

import org.tnt.account.PlayerStore;
import org.tnt.config.TNTConfig;
import org.tnt.debug.Debug;
import org.tnt.game.GameFactory;
import org.tnt.multiplayer.Hub;
import org.tnt.multiplayer.network.NetworkThread;
import org.tnt.multiplayer.network.auth.AuthHandler;

import com.spinn3r.log5j.Logger;

public class TNTServer 
{
	private final Logger log = Logger.getLogger(this.getClass());

	private TNTConfig	config;
	
	private PlayerStore store;
	
	private Hub hub;
	
	private NetworkThread network; 

	
	public TNTServer() throws Exception
	{
		log.info( "Starting server..." );
		
		long startTime = System.currentTimeMillis();

		init();
		
		Thread.sleep( 1000 );
		
		log.info( "Started in " + (System.currentTimeMillis() - startTime) + " ms." );
	}
	
	public void init()
	{

		// loading server configuration:
		config = TNTConfig.load();
		
		// loading server resources:
		GameFactory.init();
		
		// storage of player account, profile and characters
		this.store = new PlayerStore();
		
		// loading multiplayer hub:
		// manages connected players and game rooms:
		this.hub = new Hub();
		
		
		AuthHandler authenticator = new AuthHandler( store, hub );
		// starting network service:
		this.network = new NetworkThread( config.getServerConfig(), authenticator );
		
		if(true)
			Debug.init();
		
		Runtime.getRuntime().addShutdownHook( new Thread("tnt-shutdown") {
			@Override
			public void run()
			{
				hub.safeStop();
				network.safeStop();
			}
		});
	}



	public static void main( String[] args ) throws Exception
	{
		Thread.currentThread().setName( "tnt-server" );
		
		TNTServer server = new TNTServer();
		
	/*	TNTConsole console = new TNTConsole( server );
		Thread consoleThread = new Thread( console, "tnt-console" );
		consoleThread.setDaemon( true );
		consoleThread.start();*/
	}

	public PlayerStore getPlayerStore() { return store;  }
	public Hub getHub() { return hub; }
}
