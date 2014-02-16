package org.tnt.multiplayer;

import java.util.ArrayList;
import java.util.List;

import org.tnt.GameType;
import org.tnt.account.Character;
import org.tnt.account.Player;

public class GameRoom
{
	private GameType type;
	
	private int capacity;
	
	private List <Character> characters = new ArrayList <> ();


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
		characters.add( character );
	}
	
	public void removeCharacter( Player player )
	{
		Character charToRemove = null;
		for( Character character : characters)
			if(character.getPlayer() == player)
			{
				charToRemove = character;
				break;
			}
		
		characters.remove( charToRemove );
	}

	public List <Character> getCharacters() { return characters; }
}
