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

import org.tnt.IGameSimulator;
import org.tnt.IGameUpdate;
import org.tnt.account.Character;
import org.tnt.account.Player;
import org.tnt.game.SimulatorFactory;
import org.tnt.multiplayer.realtime.GoPacket;
import org.tnt.multiplayer.realtime.IngameProtocolHandler;


/**
 * Representation of game, managed by game server.
 * 
 * Keeps game state and configuration.
 */
public class MultiplayerGame
{

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

	MultiplayerGame(MultiplayerOrchestrator orchestrator, SimulatorFactory gameFactory, GameRoom room)
	{
		this.orchestrator = orchestrator;
		
		this.updates = new TIntObjectHashMap <> ();
		
		this.simulator = gameFactory.getSimulation( room.getType() );
		
		
		for(Character character : room.getCharacters().values())
		{
			int pid = nextCharId ++;
			characters.put( character, pid );
			
			updates.put( pid, new LinkedList <IGameUpdate> () );
		}
		
		simulator.setCharacters( characters );
		
		
		this.handlers = new HashMap <Character, IngameProtocolHandler> ();
	}

	void start(ExecutorService threadPool, Map <Character, IngameProtocolHandler> handlers)
	{
		this.simulatorThread  = new SimulatorThread( this, simulator );
		threadPool.submit( simulatorThread );
		
		this.handlers = handlers;
		
		this.dispatcherThread = new IngameDispatcherThread( this, handlers );
		
		for(Character character : handlers.keySet())
		{
			addUpdate( new GoPacket( characters.get( character ) ) );
		}
		
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

	public void addUpdate( IGameUpdate update )
	{
		synchronized( this.updates )
		{
			int pid = update.getPID();
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


}
