package org.tnt.account;

import java.util.ArrayList;
import java.util.List;

import org.tnt.hub.IPlayerHubDriver;

/**
 * Representation of a player registered within the server.
 * 
 * Has a system-unique {@link #id} and list of character this player manages.
 * 
 * A connected player is represented by {@link IPlayerHubDriver}
 * 
 * @author Fima
 */
public class Player implements IPlayer
{
	private final long id;

	private final List <ICharacter> characters;

	public Player(final long id)
	{
		this.id = id;

		this.characters = new ArrayList <ICharacter> ();
	}

	@Override
	public ICharacter getCharacter(final int charId)
	{
		if(characters.size() <= charId)
			return null;
		return characters.get( charId );
	}

	@Override
	public long getId() { return id; }

	@Override
	public String toString() { return "player id: " + id; }

	@Override
	public int getCharactersAmount() { return characters.size(); }

	@Override
	public void addCharacter(final ICharacter character) {
		characters.add(character);
	}
}
