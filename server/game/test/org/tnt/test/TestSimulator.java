package org.tnt.test;

import org.tnt.multiplayer.IAvatarUpdate;
import org.tnt.multiplayer.realtime.Arena;
import org.tnt.plugins.GameSimulator;
import org.tnt.plugins.IGameResults;

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
	public void step( long stepTime, int gameTime )
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