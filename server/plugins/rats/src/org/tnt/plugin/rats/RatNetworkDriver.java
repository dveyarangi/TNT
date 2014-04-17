package org.tnt.plugin.rats;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import org.tnt.multiplayer.IAvatar;
import org.tnt.multiplayer.IAvatarAction;
import org.tnt.realtime.AvatarNetworker;

public class RatNetworkDriver extends AvatarNetworker
{

	public RatNetworkDriver( Channel channel, IAvatar avatar )
	{
		super( channel, avatar );
	}

	@Override
	public int getServerPacketSize() { return 16; }

	@Override
	public int getClientPacketSize() { return 16; }

	@Override
	protected IAvatarAction parseClientUpdate( ByteBuf buffer )
	{
		// TODO Auto-generated method stub
		return null;
	}


}
