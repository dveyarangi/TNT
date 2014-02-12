package org.tnt.game.rats;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.tnt.GameType;
import org.tnt.IGameSimulator;
import org.tnt.IGameUpdate;
import org.tnt.account.Character;

public class RatsSimulation implements IGameSimulator
{
	
	private static final int CHARS_IN_RACE = 2;
	
	private Set <Character> characters = new HashSet <Character> ();
	
	private boolean isOver = true;
	

	@Override
	public void addCharacter( Character character )
	{
		characters.add( character );
	}

	@Override
	public boolean isFull()
	{
		return characters.size() == CHARS_IN_RACE;
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
	public boolean isOver() { return isOver; }

	@Override
	public GameType getType() {	return GameType.RAT_RACE; }

}
