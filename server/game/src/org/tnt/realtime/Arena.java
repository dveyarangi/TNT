/**
 * 
 */
package org.tnt.realtime;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import org.tnt.game.IGamePlugin;
import org.tnt.game.IGameResults;
import org.tnt.game.IGameSimulator;
import org.tnt.hub.GameRoom;
import org.tnt.hub.IHub;
import org.tnt.multiplayer.IArena;

import com.spinn3r.log5j.Logger;


/**
 * Representation of game, managed by game server.
 * 
 * Keeps game state and configuration.
 */
public class Arena implements IArena
{
	private final Logger log = Logger.getLogger( Arena.class );
	
	private final String gameId;
	
	/**
	 * Specific game type factory:
	 */
	private final IGamePlugin plugin;

	
	/**
	 * Game simulator, encapsulates game logic and generates updates for game clients.
	 */
	private final IGameSimulator simulator;
	
	/**
	 * Simulator thread, executes the simulator step by step
	 */
	private final SimulatorThread simulatorThread;
	
	/**
	 * Dispatcher for client updates.
	 */
	private final IngameDispatcherThread dispatcherThread;
	
	private final Avatar [] avatars;

	/**
	 * Game lifecycle listener
	 */	
	@Inject private IHub hub;

	public Arena(GameRoom room)
	{
		this.gameId = room.getGameId();
		
		this.plugin = room.getPlugin();
		
		this.avatars = new Avatar [ room.getPopulationSize() ];
		
		// creating character event queues:
		for( int pid = 0; pid < avatars.length; pid ++)
		{
			
			avatars[pid] = new Avatar( this, room.getCharacter( pid ) );
			
		}
		
		// creating game simulator:
		this.simulator = plugin.createSimulation( this );
		
		this.simulatorThread  = new SimulatorThread( this, simulator );
		
		this.dispatcherThread = new IngameDispatcherThread( avatars );
		
	}
	
	public String getId() { return gameId; }

	/**
	 * Start the game threads
	 * 
	 * @param threadPool
	 * @param handlers
	 */
	public void start( ExecutorService threadPool )
	{
		threadPool.submit( simulatorThread );
		threadPool.submit( dispatcherThread );
	}


	/**
	 * Terminate game threads and 
	 */
	public void stop()
	{
		simulatorThread.safeStop();
		dispatcherThread.safeStop();
		
		for(Avatar avatar : avatars)
		{
			avatar.stop();
		}
	}
	
	@Override
	public Avatar [] getAvatars() { return avatars; }

	public void gameOver( IGameResults results )
	{
		hub.gameOver( this, results );
	}

	/**
	 * Called when client acknowledges start of this multiplayer game.
	 * When all clients sent acknowledgment, the game simulators starts.
	 * @param pid
	 */
	public void setGameAcknowledged( Avatar avatar )
	{
		
		log.debug("Game %s character [%s] has acknowledged game start", this.toString(), avatar);
		
		// checking if all clients have acknowledged the game start:
		for(Avatar anAvatar : avatars)
		{
			if(!anAvatar.isIngame() )
			{
				return;
			}
		}

//		for(boolean ready : playerStates) if(!ready) return;

		// starting game:
		for(int pid = 0; pid < avatars.length; pid ++)
		{
			// adding the initial character state update:
			avatars[pid].putUpdate( simulator.getStartingUpdate( pid ) );
		}
			
		dispatcherThread.togglePause();
		simulatorThread.togglePause();
			
		log.debug("Game %s has started", this);
	}
	
	

	public IGamePlugin getPlugin() { return plugin; }

	@Override
	public String toString()
	{
		return new StringBuilder()
			.append(plugin.getName()).append("-").append(gameId)
			.toString();
	}
}
