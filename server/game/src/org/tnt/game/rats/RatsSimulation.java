package org.tnt.game.rats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tnt.GameType;
import org.tnt.IGameSimulator;
import org.tnt.IGameUpdate;
import org.tnt.account.Character;

public class RatsSimulation implements IGameSimulator
{
	
	private static final int CHARS_IN_RACE = 2;
	
	private Map<Character, Integer> characters = new HashMap<Character, Integer> ();
	
	private boolean isOver = true;
	

	@Override
	public void setCharacters( Map<Character, Integer> character )
	{
		this.characters.putAll( characters );
	}


	@Override
	public void init()
	{
		isOver = false;
	}

	@Override
	public List<IGameUpdate> step( long time )
	{
		List <IGameUpdate> updates = new LinkedList <IGameUpdate> ();
		
		// TODO: harr, calculation
		
		return updates;
	}

	@Override
	public int getMaxCapacity() { return CHARS_IN_RACE; }
	
	@Override
	public boolean isOver() { return isOver; }

	@Override
	public GameType getType() {	return GameType.RAT_RACE; }

}
