/**
 * 
 */
package org.tnt.multiplayer;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

import org.tnt.account.Character;
import org.tnt.game.IGamePlugin;
import org.tnt.game.IGameSimulator;
import org.tnt.multiplayer.realtime.IMultiplayerGameListener;
import org.tnt.multiplayer.realtime.IngameDispatcherThread;
import org.tnt.multiplayer.realtime.SimulatorThread;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
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
	 * Maps short ingame ids to queue of updates to be sent to the client.
	 */
	private final TIntObjectHashMap<Queue <IGameUpdate>> updates;
	
	/**
	 * Game simulator, encapsulates game logic and generates updates for game clients.
	 */
	private final IGameSimulator simulator;
	
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
	private final Map <Character, Integer> characters = new HashMap <> ();
	
	/**
	 * Characters mapped to ingame communication protocol handlers.
	 */
	private Map <Character, ICharacterDriver> drivers;
	
	private Queue actions = new ConcurrentLinkedList ;

	/**
	 * Game lifecycle listener
	 */	
	private IMultiplayerGameListener	listener;

	public MultiplayerGame(GameRoom room)
	{
		this.gameId = room.getGameId();
		
		this.plugin = room.getPlugin();
		
		this.updates = new TIntObjectHashMap <> ();
		
		// create game simulator
		this.simulator = plugin.createSimulation( room.getCharacters() );

		// register characters and ids:
		int idx = 0;
		for(Character character : room.getCharacters())
		{
			characters.put( character, idx );
			
			idx ++;
		}
		
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
	 * Put client updates for specified character id
	 * @param pid
	 * @param update
	 */
	public void addUpdate( int pid, IGameUpdate update )
	{
		synchronized( this.updates )
		{
			Queue <IGameUpdate> charUpdates  = this.updates.get( pid );
			charUpdates.add( update );
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
	
	public Map <Character, Integer> getCharacters() { return characters; }

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
		synchronized( this.updates )
		{
			// creating new updates queue for this character:
			Queue <IGameUpdate> pUpdates = new LinkedList <IGameUpdate> ();
			updates.put( pid, pUpdates );
			
			// adding the initial character state update:
			IGameUpdate update = simulator.getCharacterUpdate( pid );
			if(update == null)
			{
				log.error( "Got null update from game simulator %s", new Exception(), simulator );
				return;
			}
			
			pUpdates.add( update );
			log.debug("Game %s character [%d] has acknowledged game start", this.toString(), pid);
			
			// checking if all clients have acknowledged the game start:
			if(updates.size() == characters.size())
			{
				for(Character character : characters.keySet())
				{
					ICharacterDriver driver = drivers.get( character );
					int cpid = characters.get( character );
					driver.setStarted( updates.get( cpid ).poll() );
				}
				
				simulatorThread.togglePause();
				dispatcherThread.togglePause();
				
				log.debug("Game %s has started", this.toString());
			}
		}
	}

	/**
	 * Inform game simulator of incoming player action.
	 * @param pid
	 * @param action
	 */
	public void addCharacterAction( int pid, ICharacterAction action )
	{
		simulator.addCharacterAction( pid, action );
	}

	public IGamePlugin getPlugin() { return plugin; }

	void setListener( IMultiplayerGameListener listener ) {this.listener = listener; } 

}
