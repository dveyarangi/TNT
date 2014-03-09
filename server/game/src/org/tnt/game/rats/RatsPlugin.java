package org.tnt.game.rats;

import io.netty.channel.Channel;

import org.tnt.game.GamePlugin;
import org.tnt.game.GameSimulator;
import org.tnt.game.IGamePlugin;
import org.tnt.multiplayer.network.realtime.IngameProtocolHandler;
import org.tnt.multiplayer.realtime.Arena;
import org.tnt.multiplayer.realtime.Avatar;

@GamePlugin
public class RatsPlugin implements IGamePlugin
{
	
	public static final String NAME = "rats";

	@Override
	public String getName() { return NAME; }
	@Override
	public void init()
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public GameSimulator createSimulation( Arena game )
	{
		return new RatsSimulation( game );	
	}

	@Override
	public IngameProtocolHandler createNetworkCharacterDriver( Channel channel, Avatar avatar )
	{
		return new RatsProtocolHandler( channel, avatar );
	}

}
