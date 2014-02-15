package org.tnt.multiplayer;

import java.util.HashMap;
import java.util.Map;

import org.tnt.GameType;
import org.tnt.account.Character;
import org.tnt.account.Player;

public class GameRoom
{
	private GameType type;
	
	private int capacity;
	
	private Map <Player, Character> characters = new HashMap <Player, Character> ();


	public GameRoom( GameType type, int capacity )
	{
		this.type = type;
		this.capacity = capacity;
	}


	public GameType getType() { return type; }
	
	public boolean isFull()
	{
		return characters.size() >= capacity;
	}

	public void addCharacter( Character character )
	{
		characters.put( character.getPlayer(), character );
	}
	
	public void removeCharacter( Player player )
	{
		characters.remove( player );
	}

	public Map <Player, Character> getCharacters() { return characters; }
}
