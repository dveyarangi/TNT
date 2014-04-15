package org.tnt.test;

import io.netty.channel.Channel;

import org.tnt.game.GamePlugin;
import org.tnt.game.IGamePlugin;
import org.tnt.game.IGameSimulator;
import org.tnt.multiplayer.IArena;
import org.tnt.multiplayer.IAvatar;
import org.tnt.network.realtime.IAvatarNetworker;

@GamePlugin
public class TestGame implements IGamePlugin
{

	@Override
	public String getName()
	{
		return "tnt-test-game";
	}

	@Override
	public void init()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public IGameSimulator createSimulation( IArena game )
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IAvatarNetworker createAvatarNetworker( Channel channel, IAvatar avatar )
	{
		// TODO Auto-generated method stub
		return null;
	}

}
