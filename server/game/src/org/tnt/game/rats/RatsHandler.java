package org.tnt.game.rats;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import org.tnt.multiplayer.ICharacterAction;
import org.tnt.multiplayer.MultiplayerGame;
import org.tnt.multiplayer.realtime.IngameProtocolHandler;

public class RatsHandler extends IngameProtocolHandler
{

	public RatsHandler( Channel channel, MultiplayerGame multiplayer, int pid )
	{
		super( channel, multiplayer, pid );
	}

	@Override
	public int getServerPacketSize() { return 16; }

	@Override
	public int getClientPacketSize() { return 16; }

	@Override
	protected ICharacterAction parseClientUpdate( ByteBuf buffer )
	{
		// TODO Auto-generated method stub
		return null;
	}

}
