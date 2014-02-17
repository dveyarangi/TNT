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

import org.tnt.GameType;
import org.tnt.IGameSimulator;
import org.tnt.IGameUpdate;
import org.tnt.account.Character;
import org.tnt.account.Player;
import org.tnt.game.GameFactory;
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

	private MultiplayerOrchestrator orchestrator;
	
	/**
	 * Characters participating in this game
	 */
	private TIntObjectHashMap<Queue <IGameUpdate>> updates;
	
	private IGameSimulator simulator;
	
	private IngameDispatcherThread dispatcherThread;
	private SimulatorThread simulatorThread;
	
	private int nextCharId = 1;
	
	private Map <Character, Integer> characters = new HashMap <> ();
	
	private Map <Character, IngameProtocolHandler> handlers;

	MultiplayerGame(MultiplayerOrchestrator orchestrator, GameFactory gameFactory, GameRoom room)
	{
		this.orchestrator = orchestrator;
		
		this.updates = new TIntObjectHashMap <> ();
		
		this.simulator = gameFactory.getSimulation( room.getType() );
		
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


	void start(ExecutorService threadPool, Map <Character, IngameProtocolHandler> handlers)
	{
		this.simulatorThread  = new SimulatorThread( this, simulator );
		threadPool.submit( simulatorThread );
		
		this.handlers = handlers;
		
		this.dispatcherThread = new IngameDispatcherThread( this, handlers );
		
/*		for(Character character : handlers.keySet())
		{
			int pid = characters.get( character );
			addUpdate( pid, new GoPacket() );
		}*/
		
		threadPool.submit( dispatcherThread );
	}
    
    public void setAcknowledged(Player player)
    {
    	
    }
    
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

	public void addUpdate( int pid, IGameUpdate update )
	{
		synchronized( this.updates )
		{
			Queue <IGameUpdate> charUpdates  = this.updates.get( pid );
			charUpdates.add( update );
		}
	}
	
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
		orchestrator.gameOver( this );
	}

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


	public void addCharacterAction( int pid, ICharacterAction action )
	{
		simulator.addCharacterAction( pid, action );
	}


}
