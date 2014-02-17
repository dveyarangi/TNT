package org.tnt.multiplayer;

import java.util.ArrayList;
import java.util.List;

import org.tnt.account.Character;
import org.tnt.account.Player;
import org.tnt.game.GameType;

/**
 * Game room for characters to wait for additional participants and game start.
 * 
 * 
 * @author fimar
 */
public class GameRoom
{
	/** 
	 * game type
	 */
	private GameType type;
	
	/**
	 * room capacity
	 */
	private int capacity;
	
	/**
	 * joined characters
	 */
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
}
