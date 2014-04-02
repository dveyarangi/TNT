package org.tnt.account;

import java.util.ArrayList;
import java.util.List;

import org.tnt.multiplayer.IPlayerHubDriver;

/**
 * Representation of a player registered within the server.
 * 
 * Has a system-unique {@link #id} and list of character this player manages.
 * 
 * A connected player is represented by {@link IPlayerHubDriver}
 * 
 * @author Fima
 */
public class Player
{
	private final long id;
	
	private final List <Character> characters;
	
	public Player(long id)
	{
		this.id = id;

		this.characters = new ArrayList <Character> ();
	}
	
	public Character getCharacter(int charId) 
	{
		if(characters.size() <= charId)
			return null;
		return characters.get( charId ); 
	}
	
	public List <Character> getCharacters() { return characters; }

	public long getId() { return id; }
	
	@Override
	public String toString() { return "player id: " + id; } 
}
