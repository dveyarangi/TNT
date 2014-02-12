package org.tnt.account;

import java.util.ArrayList;
import java.util.List;

public class Player
{
	private long id;
	
	private List <Character> characters;
	
	public Player(long id)
	{
		this.id = id;

		this.characters = new ArrayList <Character> ();
	}
	
	public List <Character> getCharacters() { return characters; }

	public long getId() { return id; }
}
