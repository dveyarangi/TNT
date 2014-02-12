/**
 * 
 */
package org.tnt.multiplayer;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.tnt.IGameSimulator;
import org.tnt.IGameUpdate;
import org.tnt.account.Character;
import org.tnt.account.Player;


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
	private Map <Character, Queue <IGameUpdate>> updates;
	
	private DispatcherThread dispatcherThread;
	private SimulatorThread simulatorThread;
	
	private int nextCharId = 1;
	
	private Map <Character, Integer> characters = new HashMap <Character, Integer> ();

	MultiplayerGame(MultiplayerOrchestrator orchestrator, IGameSimulator simulation)
	{
		this.orchestrator = orchestrator;
		
		this.simulatorThread  = new SimulatorThread( this, simulation );
		this.dispatcherThread = new DispatcherThread( this );
		
		this.updates = new IdentityHashMap <Character, Queue<IGameUpdate>> (); 
	}
	
	void addCharacter(Character character)
	{
		if(characters.containsKey( character ))
			throw new IllegalArgumentException("Character " + character + " is already in game " + this);
		
		characters.put( character, nextCharId ++ );
		
		updates.put( character, new LinkedList <IGameUpdate> () );
		
		simulatorThread.getSimulator().addCharacter( character );

	}
	
	void ready()
	{
		for(Character character : getCharacters())
		{
			Channel channel = orchestrator.getPlayerRegistery().getChannel( character.getPlayer() );
		}
		
	}

	public void gameAcknowledged( Player player )
	{
		GameProtocolHandler handler = 
				orchestrator.getPlayerRegistery().getPlayerHandler( );
		
	}	
	void start(ExecutorService threadPool)
	{
		
		Future simulatorfuture = threadPool.submit( simulatorThread );
		Future dispatchFutire  = threadPool.submit( dispatcherThread );
	}
	
	public Queue<IGameUpdate> getUpdates(Character character)
	{
		synchronized( updates )
		{
			Queue <IGameUpdate> charUpdates  = this.updates.get( character );
			if(!this.updates.isEmpty())
				this.updates.put( character, new LinkedList <IGameUpdate> () );
			
			
			return charUpdates;
		}
	}

	public void addUpdate( IGameUpdate update )
	{
		synchronized( this.updates )
		{
			Character character = update.getCharacter();
			Queue <IGameUpdate> charUpdates  = this.updates.get( character );
			charUpdates.add( update );
		}
	}
	
	public void stop()
	{
		simulatorThread.safeStop();
		dispatcherThread.safeStop();
	}

	public IGameSimulator getSimulator()
	{
		return simulatorThread.getSimulator();
	}
	
	Set <Character> getCharacters() { return characters.keySet(); }
	
	MultiplayerOrchestrator getOrchestrator() { return orchestrator; }

	public void gameOver()
	{
		orchestrator.gameOver( this );
	}


}
