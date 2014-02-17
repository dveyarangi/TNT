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
import org.tnt.game.GameFactory;
import org.tnt.game.GameType;
import org.tnt.game.IGameSimulator;
import org.tnt.multiplayer.realtime.IngameProtocolHandler;

import com.spinn3r.log5j.Logger;


/**
 * Representation of game, managed by game server.
 * 
 * Keeps game state and configuration.
 */
public class MultiplayerGame
{
	private Logger log = Logger.getLogger( MultiplayerGame.class );

	/**
	 * Multiplayer hub
	 */
	private MultiplayerHub hub;
	
	/**
	 * Maps short ingame ids to queue of updates to be sent to the client.
	 */
	private TIntObjectHashMap<Queue <IGameUpdate>> updates;
	
	/**
	 * Game simulator, encapsulates game logic and generates updates for game clients.
	 */
	private IGameSimulator simulator;
	
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
	private Map <Character, Integer> characters = new HashMap <> ();
	
	/**
	 * Characters mapped to ingame communication protocol handlers.
	 */
	private Map <Character, IngameProtocolHandler> handlers;

	MultiplayerGame(MultiplayerHub hub, GameFactory gameFactory, GameRoom room)
	{
		this.hub = hub;
		
		this.updates = new TIntObjectHashMap <> ();
		
		// create game simulator
		this.simulator = gameFactory.getSimulation( room.getType() );

		// register characters and ids:
		int idx = 0;
		for(Character character : room.getCharacters())
		{
			characters.put( character, idx );
			
			idx ++;
		}
		
		simulator.setCharacters( room.getCharacters() );
		
		
		this.handlers = new HashMap <Character, IngameProtocolHandler> ();
	}
	

	public GameType getType() { return simulator.getType();	}

	/**
	 * Start the game threads
	 * 
	 * @param threadPool
	 * @param handlers
	 */
	void start(ExecutorService threadPool, Map <Character, IngameProtocolHandler> handlers)
	{
		this.simulatorThread  = new SimulatorThread( this, simulator );
		threadPool.submit( simulatorThread );
		
		this.handlers = handlers;
		
		this.dispatcherThread = new IngameDispatcherThread( this, handlers );

		
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
		for(IngameProtocolHandler handler : handlers.values())
		{
			handler.stop();
		}
	}
	
	public Map <Character, Integer> getCharacters() { return characters; }

	public void gameOver()
	{
		hub.gameOver( this );
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
					IngameProtocolHandler handler = handlers.get( character );
					int cpid = characters.get( character );
					handler.setStarted( updates.get( cpid ).poll() );
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


}
