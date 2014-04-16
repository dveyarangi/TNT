package org.tnt.hub;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.tnt.account.Player;
import org.tnt.game.IGameResults;
import org.tnt.network.IPlayerConnections;
import org.tnt.realtime.Arena;
import org.tnt.realtime.Avatar;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.spinn3r.log5j.Logger;

/**
 * Hub assessment thread.
 * TODO: thread management 
 * 
 * 
 * @author Fima
 *
 */
@Singleton
public class HubThread implements IHubThread
{
	/**
	 * A logger
	 */
	private final Logger log = Logger.getLogger(this.getClass());

	
	private IHub hub;
	
	/**
	 * executor pool for game threads
	 */
	private final ExecutorService threadPool = Executors.newCachedThreadPool();
	
	/**
	 * Registry of games in progress
	 */
	private final Map <Player, Arena> runningGames = new IdentityHashMap<> ();
	
	private volatile boolean isAlive = false;
	
	private final IPlayerConnections connections;
	@Inject 
	public HubThread (IPlayerConnections connections)
	{
		this.connections = connections;
		
	}

	@Override
	public void init()
	{
		log.debug( "Starting multiplayer hub thread..." );
		new Thread(this, "tnt-hub") .start();
	}
	
	/* (non-Javadoc)
	 * @see org.tnt.multiplayer.IHubThread#startGame(org.tnt.multiplayer.GameRoom)
	 */
	@Override
	public void startGame(GameRoom room )
	{

		Arena game = new Arena( room );
		
		synchronized(runningGames)
		{
			
			for(int pid = 0; pid < game.getAvatars().length; pid ++)
			{
				Avatar avatar = game.getAvatars()[pid];
				Player player = avatar.getPlayer();
				
				IPlayerHubDriver playerDriver = connections.getPlayerDriver( player );
				avatar.gameCreated ( playerDriver.playerInGame( room, avatar ) );
				
					
				// updating running games registry:
				runningGames.put( player, game );
			}
		}	
		
		// starting game:
		game.start( threadPool );
	}

	@Override
	public void run()
	{
		isAlive = true;
		while(isAlive)
		{
			
			
			try
			{
				Thread.sleep( 50 );
			}
			catch( InterruptedException e )
			{
				isAlive = false;
				log.error( e );
				break;
			}
		}
		isAlive = false;
	}

	/* (non-Javadoc)
	 * @see org.tnt.multiplayer.IHubThread#gameOver(org.tnt.multiplayer.realtime.Arena, org.tnt.multiplayer.IGameResults)
	 */
	@Override
	public void gameOver( Arena game, IGameResults results )
	{
		
		synchronized(runningGames)
		{
			for(Avatar avatar : game.getAvatars())
			{
				runningGames.remove( avatar.getPlayer() );
				
				IPlayerHubDriver driver = connections.getPlayerDriver( avatar.getPlayer() );
				
				driver.gameEnded( results );
				
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.tnt.multiplayer.IHubThread#safeStop()
	 */
	@Override
	public void safeStop()
	{
		this.isAlive = false;
		
		for(Arena game : runningGames.values())
		{
			game.stop();
		}
	}

}
