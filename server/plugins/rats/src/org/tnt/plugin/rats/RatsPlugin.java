package org.tnt.plugin.rats;

import io.netty.channel.Channel;

import org.tnt.multiplayer.IArena;
import org.tnt.multiplayer.IAvatar;
import org.tnt.network.realtime.IAvatarNetworker;
import org.tnt.plugins.GamePlugin;
import org.tnt.plugins.IGamePlugin;
import org.tnt.plugins.IGameSimulator;

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
	public IGameSimulator createSimulation( IArena game )
	{
		return new RatsSimulation( game );	
	}

	@Override
	public IAvatarNetworker createAvatarNetworker( Channel channel, IAvatar avatar )
	{
		return new RatNetworkDriver( channel, avatar );
	}

}
