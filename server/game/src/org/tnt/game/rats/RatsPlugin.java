package org.tnt.game.rats;

import io.netty.channel.Channel;

import org.tnt.game.GamePlugin;
import org.tnt.game.GameSimulator;
import org.tnt.game.IGamePlugin;
import org.tnt.multiplayer.MultiplayerGame;
import org.tnt.multiplayer.realtime.IngameProtocolHandler;

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
	public GameSimulator createSimulation( MultiplayerGame game )
	{
		return new RatsSimulation( game );	
	}

	@Override
	public IngameProtocolHandler createNetworkCharacterDriver( Channel channel, MultiplayerGame game, int pid )
	{
		return new RatsProtocolHandler(channel, game, pid);
	}

}
