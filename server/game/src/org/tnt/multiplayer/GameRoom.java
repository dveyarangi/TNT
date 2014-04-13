package org.tnt.multiplayer;

import java.util.ArrayList;

import org.tnt.account.Character;
import org.tnt.account.Player;
import org.tnt.plugins.IGamePlugin;

/**
 * Game room for characters to wait for additional participants and game start.
 * 
 * Manages modifiable mapping of players to selected ingame characters;
 * 
 * @author fimar
 */
public class GameRoom extends HubRoom
{
	
	/**
	 * room capacity
	 */
	private final int capacity;
	
	/**
	 * Game served by this room
	 */
	private final IGamePlugin plugin;
	
	private ArrayList <Character> characters;


	public GameRoom( IGamePlugin plugin, int capacity )
	{
		this.plugin = plugin;
		this.capacity = capacity;
		
		this.characters = new ArrayList <Character> ();
	}
	
	public boolean isFull()
	{
		return getParticipants().size() >= capacity;
	}

	public String getType()	{ return plugin.getName(); }

	public IGamePlugin getPlugin() { return plugin; }

	@Override
	public void addParticipant( Player player )
	{
		super.addParticipant( player );
		characters.add( player.getCharacter( 0 ) );
	}

	@Override
	public void removeParticipant( Player player )
	{
		super.removeParticipant( player );
		characters.remove( player.getCharacter( 0 ) );
	}

	/**
	 * @param pid
	 * @return
	 */
	public Character getCharacter( int pid )
	{
		return characters.get( pid );
	}

}
