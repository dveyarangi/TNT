package org.tnt.game.rats;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.tnt.account.Character;
import org.tnt.game.GameType;
import org.tnt.game.IGameSimulator;
import org.tnt.multiplayer.ICharacterAction;
import org.tnt.multiplayer.IGameUpdate;

public class RatsSimulation implements IGameSimulator
{
	
	private static final int CHARS_IN_RACE = 2;
	
	private List <Character> characters = new ArrayList<Character> ();
	
	private boolean isOver = true;
	
	private int time = 0;
	

	@Override
	public void setCharacters( List <Character> characters )
	{
		this.characters.addAll( characters );
	}


	@Override
	public void init()
	{
		isOver = false;
	}

	@Override
	public void step( long time, TIntObjectHashMap <IGameUpdate> updates)
	{
		this.time = (int)time;
		
		int idx = 0;
		for(Character character : characters)
		{
			updates.put( idx, getCharacterUpdate( idx ) );
			idx ++;
		}

	}

	@Override
	public int getMaxCapacity() { return CHARS_IN_RACE; }
	
	@Override
	public boolean isOver() { return isOver; }

	@Override
	public GameType getType() {	return GameType.RAT_RACE; }


	@Override
	public void addCharacterAction( int pid, ICharacterAction action )
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public IGameUpdate getCharacterUpdate( int pid )
	{
		Character character = characters.get( pid );
		return new ServerPacket( pid, time, 0, CharacterAction.START );
	}

}
