package org.tnt.test;

import io.netty.channel.Channel;

import org.tnt.game.GamePlugin;
import org.tnt.game.GameSimulator;
import org.tnt.game.IGamePlugin;
import org.tnt.multiplayer.network.realtime.IngameProtocolHandler;
import org.tnt.multiplayer.realtime.Arena;
import org.tnt.multiplayer.realtime.Avatar;

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
	public GameSimulator createSimulation( Arena game )
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IngameProtocolHandler createNetworkCharacterDriver( Channel channel, Avatar avatar )
	{
		// TODO Auto-generated method stub
		return null;
	}

}
