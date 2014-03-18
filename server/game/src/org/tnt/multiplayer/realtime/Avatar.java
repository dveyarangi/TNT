package org.tnt.multiplayer.realtime;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.tnt.account.Character;

/**
 * Ingame representation of {@link Arena} participant.
 * 
 * Manages messages queues from character driver to game simulation and vice versa.
 * 
 * @author Fima
 *
 */
public class Avatar extends Character
{
	/**
	 * Avatar controller, provides avatar actions.
	 */
	private ICharacterDriver driver;
	
	/**
	 * Queue of actions received from avatar driver.
	 */
	private final Queue <IAvatarAction> actions;
	
	/**
	 * Maps short ingame ids to queues of updates to be sent to the client.
	 */
	private final Queue <IAvatarUpdate> updates;
	
	/**
	 * Turns true once the avatar driver had acknowledged game start.
	 */
	private boolean ingame;

	/**
	 * Game arena this player avatar is appears in.
	 */
	private final Arena arena;
	
	public Avatar( Arena arena, Character character )
	{
		super( character.getId(), character.getPlayer() );
		
		this.arena = arena;
		
		this.actions = new ConcurrentLinkedQueue <> ();
		this.updates = new ConcurrentLinkedQueue <> ();
		
		this.ingame = false;
		
	}

	public void gameCreated( ICharacterDriver driver )
	{
		this.driver = driver;
	}
	public void gameAcknowledged()
	{
		ingame = true;
		arena.setGameAcknowledged( this );
	}
	
	public boolean isIngame() {	return ingame; }




	public void stop()
	{
		driver.stop();
	}





	public void putActions( IAvatarAction action )
	{
		actions.add( action );
	}
	
	void flushUpdates()
	{
		while(!updates.isEmpty())
		{
			driver.update( updates.poll() );
		}
	}


	public void addUpdate( IAvatarUpdate update )
	{
		updates.add( update );
	}


}
