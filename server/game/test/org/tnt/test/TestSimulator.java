package org.tnt.test;

import org.tnt.game.GameSimulator;
import org.tnt.multiplayer.IGameResults;
import org.tnt.multiplayer.realtime.Arena;
import org.tnt.multiplayer.realtime.IAvatarUpdate;

public class TestSimulator extends GameSimulator
{
	
	public TestSimulator( Arena game )
	{
		super( game );
	}

	@Override
	public int getMaxCapacity()
	{
		return 2;
	}

	@Override
	public void step( long stepTime, long gameTime )
	{
	}

	@Override
	public void destroy()
	{
	}

	@Override
	public IAvatarUpdate getStartingUpdate( int pid )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IGameResults isOver()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
