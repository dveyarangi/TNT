package org.tnt.plugin.rats;

import io.netty.channel.Channel;

import org.tnt.ai.IAvatarAISettings;
import org.tnt.game.GamePlugin;
import org.tnt.game.IGamePlugin;
import org.tnt.game.IGameSimulator;
import org.tnt.multiplayer.IArena;
import org.tnt.multiplayer.IAvatar;
import org.tnt.multiplayer.IAvatarDriver;
import org.tnt.network.realtime.IAvatarNetworker;

@GamePlugin
public class RatsPlugin implements IGamePlugin
{
	@Override
	public String getName() { return "rats"; }
	@Override
	public void init()
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public IGameSimulator createSimulation( IArena game )
	{
		return new RatsSimulation( game );	
	}

	@Override
	public IAvatarNetworker createAvatarNetworker( Channel channel, IAvatar avatar )
	{
		return new RatNetworkDriver( channel, avatar );
	}
	@Override
	public IAvatarDriver createAvatarAI(IAvatar avatar,
			IAvatarAISettings settings) {
		// TODO Auto-generated method stub
		return null;
	}

}
