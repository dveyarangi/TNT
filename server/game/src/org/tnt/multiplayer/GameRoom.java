package org.tnt.multiplayer;

import java.util.ArrayList;
import java.util.List;

import org.tnt.account.Character;
import org.tnt.account.Player;
import org.tnt.game.IGamePlugin;
import org.tnt.util.IDProvider;

/**
 * Game room for characters to wait for additional participants and game start.
 * 
 * 
 * @author fimar
 */
public class GameRoom
{
	
	/**
	 * Game system-unique identifier for persistence and reference.
	 */
	private final String gameId;
	
	/**
	 * Room creation time
	 * TODO: use to monitor rooms that are pending for too long.
	 */
	private final long creationTime = System.currentTimeMillis();
	
	/**
	 * room capacity
	 */
	private final int capacity;
	
	/**
	 * joined characters
	 */
	private final List <Character> characters = new ArrayList <> ();
	
	/**
	 * Game served by this room
	 */
	private final IGamePlugin plugin;


	public GameRoom( IGamePlugin plugin, int capacity )
	{
		this.gameId = IDProvider.generateGameId();
		this.plugin = plugin;
		this.capacity = capacity;
	}

	public String getGameId() {	return gameId; }
	
	public boolean isFull()
	{
		return characters.size() >= capacity;
	}

	public void addCharacter( Character character )
	{
		characters.add( character );
	}
	
	public void removeCharacter( Player player )
	{
		Character charToRemove = null;
		for( Character character : characters)
		{
			if(character.getPlayer() == player)
			{
				charToRemove = character;
				break;
			}
		}
		
		characters.remove( charToRemove );
	}

	/**
	 * Retrieves characters list.
	 * Order of characters in this list determines character's short ingame identifier
	 * @return
	 */
	public List <Character> getCharacters() { return characters; }

	public String getType()	{ return plugin.getName(); }

	public IGamePlugin getPlugin() { return plugin; }


}
