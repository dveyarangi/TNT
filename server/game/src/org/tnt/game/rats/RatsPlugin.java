package org.tnt.game.rats;

import io.netty.channel.Channel;

import java.util.List;

import org.tnt.account.Character;
import org.tnt.game.IGamePlugin;
import org.tnt.game.IGameSimulator;
import org.tnt.multiplayer.MultiplayerGame;
import org.tnt.multiplayer.realtime.IngameProtocolHandler;

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
	public IGameSimulator createSimulation( List <Character> characters )
	{
		return new RatsSimulation( characters );	
	}

	@Override
	public IngameProtocolHandler createNetworkCharacterDriver( Channel channel, MultiplayerGame game, int pid )
	{
		return new RatsProtocolHandler(channel, game, pid);
	}

}
