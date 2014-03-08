/**
 * 
 */
package org.tnt.multiplayer;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

import org.tnt.account.Character;
import org.tnt.game.GameSimulator;
import org.tnt.game.IGamePlugin;
import org.tnt.multiplayer.realtime.IMultiplayerGameListener;
import org.tnt.multiplayer.realtime.IngameDispatcherThread;
import org.tnt.multiplayer.realtime.SimulatorThread;

import com.spinn3r.log5j.Logger;


/**
 * Representation of game, managed by game server.
 * 
 * Keeps game state and configuration.
 */
public class MultiplayerGame
{
	private final Logger log = Logger.getLogger( MultiplayerGame.class );
	
	private final String gameId;
	
	/**
	 * Specific game type factory:
	 */
	private final IGamePlugin plugin;

	
	/**
	 * Game simulator, encapsulates game logic and generates updates for game clients.
	 */
	private final GameSimulator simulator;
	
	/**
	 * Simulator thread, executes the simulator step by step
	 */
	private SimulatorThread simulatorThread;
	
	/**
	 * Dispatcher for client updates.
	 */
	private IngameDispatcherThread dispatcherThread;
	

	/**
	 * Participating characters and their corresponding short ids.
	 */
	private final List <Character> characters;
	
	/**
	 * Characters mapped to ingame communication protocol handlers.
	 */
	private Map <Character, ICharacterDriver> drivers;
	/**
	 * Game lifecycle listener
	 */	
	private IMultiplayerGameListener	listener;
	
	/**
	 * Maps short ingame ids to queues of actions received from client.
	 */
	private final TIntObjectHashMap<Queue <ICharacterAction>> actions;
	
	/**
	 * Maps short ingame ids to queues of updates to be sent to the client.
	 */
	private final TIntObjectHashMap<Queue <IGameUpdate>> updates;
	
	private final boolean [] playerStates;


	public MultiplayerGame(GameRoom room)
	{
		this.gameId = room.getGameId();
		
		this.plugin = room.getPlugin();
		
		this.characters = new LinkedList <> ();
		
		// creating character event queues:
		this.updates = new TIntObjectHashMap <> ();
		this.actions = new TIntObjectHashMap <> ();

		int charNum = room.getCharacters().size();
		
		playerStates = new boolean[charNum];
		
		// creating character event queues:
		for( int pid = 0; pid < charNum; pid ++)
		{
			characters.add( room.getCharacters().get( pid ) );
			
			actions.put( pid,  new ConcurrentLinkedQueue <ICharacterAction> () );
			updates.put( pid,  new ConcurrentLinkedQueue <IGameUpdate> () );
			
			playerStates[pid] = false;
		}
		
		// creating game simulator:
		this.simulator = plugin.createSimulation( this );
		
	}
	
	public String getId() { return gameId; }

	/**
	 * Start the game threads
	 * 
	 * @param threadPool
	 * @param handlers
	 */
	void start(ExecutorService threadPool, Map <Character, ICharacterDriver> drivers)
	{

		
		this.simulatorThread  = new SimulatorThread( this, simulator );
		threadPool.submit( simulatorThread );
		
		this.drivers = drivers;
		
		this.dispatcherThread = new IngameDispatcherThread( this, drivers );

		
		threadPool.submit( dispatcherThread );
	}

	/**
	 * Retrieve client updates for specified character id
	 * @param pid
	 * @return
	 */
	public Queue<IGameUpdate> getUpdates(int pid)
	{
		synchronized( updates )
		{
			Queue <IGameUpdate> charUpdates  = this.updates.get( pid );
			if(!this.updates.isEmpty())
			{
				this.updates.put( pid, new LinkedList <IGameUpdate> () );
			}
			
			
			return charUpdates;
		}
	}

	/**
	 * Terminate game threads and 
	 */
	public void stop()
	{
		simulatorThread.safeStop();
		for(ICharacterDriver driver : drivers.values())
		{
			driver.stop();
		}
	}
	
	public List <Character> getCharacters() { return characters; }

	public void gameOver( IGameResults results )
	{
		listener.gameOver( this, results );
	}

	/**
	 * Called when client acknowledges start of this multiplayer game.
	 * When all clients sent acknowledgment, the game simulators starts.
	 * @param pid
	 */
	public void setGameAcknowledged( int pid )
	{
		
		log.debug("Game %s character [%d] has acknowledged game start", this.toString(), pid);

		playerStates[pid] = true;
		// checking if all clients have acknowledged the game start:
		for(boolean ready : playerStates) if(!ready) return;

		// starting game:
		for(int cpid = 0; cpid < playerStates.length; cpid ++)
		{
			// adding the initial character state update:
			putCharacterUpdate( cpid, simulator.getStartingUpdate( cpid ) );
		}
			
		dispatcherThread.togglePause();
		simulatorThread.togglePause();
			
		log.debug("Game %s has started", this.toString());
	}
	
	

	public IGamePlugin getPlugin() { return plugin; }

	void setListener( IMultiplayerGameListener listener ) {this.listener = listener; }

	public void putCharacterUpdate( int pid, IGameUpdate update )
	{
		if(update == null)
		{
			log.error( "Got null update from game simulator [%s], player id %d", simulator, pid );
			return;
		}
		updates.get( pid ).add( update );
	}

	public Queue<ICharacterAction> getCharacterAction( int pid )
	{
		return actions.get( pid );
	}

	public void putCharacterAction( int pid, ICharacterAction action )
	{
		actions.get( pid ).add( action );
	} 

}
