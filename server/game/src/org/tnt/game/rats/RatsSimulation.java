package org.tnt.game.rats;

import org.tnt.game.GameSimulator;
import org.tnt.multiplayer.IGameResults;
import org.tnt.multiplayer.IGameUpdate;
import org.tnt.multiplayer.MultiplayerGame;

public class RatsSimulation extends GameSimulator
{
	
	private static final int CHARS_IN_RACE = 2;
	
	private final IGameResults results = null;
	
	public RatsSimulation( MultiplayerGame game )
	{
		super( game );
	}


	@Override
	public void step(long stepTime, long gameTime)
	{
		int time = (int)gameTime;
		
		for(int pid = 0; pid < getMaxCapacity(); pid ++)
		{
			putCharacterUpdate( pid, createCharacterUpdate( pid, time ) );
			pid ++;
		}

	}


	@Override
	public IGameUpdate getStartingUpdate( int pid )
	{
		return new ServerPacket(pid, 0, 0, CharacterAction.START);
	}

	private IGameUpdate createCharacterUpdate( int pid, int time )
	{
		return new ServerPacket(pid, time, 0, CharacterAction.START);
	}


	@Override
	public void destroy()
	{
	}

	@Override
	public int getMaxCapacity() { return CHARS_IN_RACE; }
	
	@Override
	public IGameResults isOver() { return results; }



}
