package org.tnt.game.rats;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import org.tnt.multiplayer.network.realtime.IngameProtocolHandler;
import org.tnt.multiplayer.realtime.Avatar;
import org.tnt.multiplayer.realtime.IAvatarAction;

public class RatsProtocolHandler extends IngameProtocolHandler
{

	public RatsProtocolHandler( Channel channel, Avatar avatar )
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
