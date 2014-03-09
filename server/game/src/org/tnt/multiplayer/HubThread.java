package org.tnt.multiplayer;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.tnt.account.Player;
import org.tnt.multiplayer.realtime.Arena;
import org.tnt.multiplayer.realtime.Avatar;
import org.tnt.multiplayer.realtime.IArenaListener;

import com.spinn3r.log5j.Logger;

/**
 * Hub assessment thread.
 * TODO: thread management 
 * 
 * 
 * @author Fima
 *
 */
public class HubThread extends Thread implements IArenaListener
{
	/**
	 * A logger
	 */
	private final Logger log = Logger.getLogger(this.getClass());

	
	private final Hub hub;
	
	/**
	 * executor pool for game threads
	 */
	private final ExecutorService threadPool = Executors.newCachedThreadPool();
	
	/**
	 * Registry of games in progress
	 */
	private final Map <Player, Arena> runningGames = new IdentityHashMap<> ();
	
	private volatile boolean isAlive = false;

	public HubThread (Hub hub)
	{
		this.hub = hub;
	}

	/**
	 * Starts multiplayer game from the specified room
	 * @param gameroom
	 */
	public void startGame(GameRoom room )
	{

		Arena game = new Arena( room );

		game.setListener( this );
		
		synchronized(runningGames)
		{
			
			for(int pid = 0; pid < game.getAvatars().length; pid ++)
			{
				Avatar avatar = game.getAvatars()[pid];
				Player player = avatar.getPlayer();
				
				IPlayerDriver playerDriver = hub.getPlayer( player );
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

	@Override
	public void gameOver( Arena game, IGameResults results )
	{
		
		synchronized(runningGames)
		{
			for(Avatar avatar : game.getAvatars())
			{
				runningGames.remove( avatar.getPlayer() );
				
				IPlayerDriver driver = hub.getPlayer( avatar.getPlayer() );
				
				driver.gameEnded( results );
				
				driver.playerInHub( hub );
				
			}
		}
	}

	public void safeStop()
	{
		this.isAlive = false;
		
		for(Arena game : runningGames.values())
		{
			game.stop();
		}
	}
}
