package org.tnt.game.rats;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

import org.tnt.account.Character;
import org.tnt.game.IGameSimulator;
import org.tnt.multiplayer.ICharacterAction;
import org.tnt.multiplayer.IGameResults;
import org.tnt.multiplayer.IGameUpdate;

public class RatsSimulation implements IGameSimulator
{
	
	private static final int CHARS_IN_RACE = 2;
	
	private final List <Character> characters = new ArrayList<Character> ();
	
	private final IGameResults results = null;
	
	private int time = 0;
	
	public RatsSimulation( List <Character> characters )
	{
		this.characters.addAll( characters );
	}


	@Override
	public void init()
	{
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
	public void destroy()
	{
	}

	@Override
	public int getMaxCapacity() { return CHARS_IN_RACE; }
	
	@Override
	public IGameResults isOver() { return results; }

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
