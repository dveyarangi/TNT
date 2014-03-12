package org.tnt.game.rats;

import org.tnt.game.GameSimulator;
import org.tnt.multiplayer.IGameResults;
import org.tnt.multiplayer.realtime.Arena;
import org.tnt.multiplayer.realtime.IAvatarUpdate;

public class RatsSimulation extends GameSimulator
{
	
	private static final int CHARS_IN_RACE = 2;
	
	private IGameResults results = null;
	
	public RatsSimulation( Arena game )
	{
		super( game );
	}


	@Override
	public void step(long stepTime, int gameTime)
	{
		
		for(int pid = 0; pid < getAvatars().length; pid ++)
		{
			getAvatars()[pid].addUpdate( createCharacterUpdate( pid, gameTime ) );

		}
		
		log.debug( "Hola from rats simulator!" );
		
		if(gameTime > 20)
			results = new IGameResults() {};
	}


	@Override
	public IAvatarUpdate getStartingUpdate( int pid )
	{
		return new ServerPacket(pid, 0, 0, CharacterAction.START);
	}

	private IAvatarUpdate createCharacterUpdate( int pid, int time )
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
