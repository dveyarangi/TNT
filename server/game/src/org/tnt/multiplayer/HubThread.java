package org.tnt.multiplayer;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.tnt.account.Character;
import org.tnt.account.Player;
import org.tnt.multiplayer.realtime.IMultiplayerGameListener;

import com.spinn3r.log5j.Logger;

/**
 * Hub assessment thread.
 * TODO: thread management 
 * 
 * 
 * @author Fima
 *
 */
public class HubThread extends Thread implements IMultiplayerGameListener
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
	private final Map <Player, MultiplayerGame> runningGames = new IdentityHashMap<> ();
	
	private volatile boolean isAlive = false;

	public HubThread (Hub hub)
	{
		this.hub = hub;
	}

	/**
	 * Starts multiplayer game from the specified room
	 * @param gameroom
	 */
	public void startGame(MultiplayerGame game )
	{
		game.setListener( this );
		
		Map <Character, ICharacterDriver> handlers = new HashMap <> ();
		
		synchronized(runningGames)
		{
			int pid = 0;
			for(Character character : game.getCharacters())
			{
				Player player = character.getPlayer();
				
				IPlayerDriver playerDriver = hub.getPlayer( player );
				
				// sending game ready to all participants:
				ICharacterDriver charDriver = playerDriver.gameStarted( game, pid );
				
				/////////////////////////////////////////////////////////////////////
				// this was the last admin message, now real-time protocol starts
				
				// swapping to real time protocol:
				handlers.put( character, charDriver );
				
				// updating running games registry:
				runningGames.put( player, game );
				
				pid ++;
			}
		}	
		
		// starting game:
		game.start( threadPool, handlers );
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
	public void gameOver( MultiplayerGame game, IGameResults results )
	{
		
		synchronized(runningGames)
		{
			for(Character character : game.getCharacters())
			{
				runningGames.remove( character.getPlayer() );
				
				IPlayerDriver driver = hub.getPlayer( character.getPlayer() );
				
				driver.gameEnded( results );
				
			}
		}
	}

	{
		// TODO Auto-generated method stub
		
	}
}
