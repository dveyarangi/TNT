package org.tnt.realtime;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

import org.tnt.multiplayer.IAvatarUpdate;
import org.tnt.realtime.IServerPacket;

/**
 * This packet is sent by server to order all clients to start game simulation.
 * TODO: actually, not.
 * 
 * @author fimar
 */
public class GoPacket implements IAvatarUpdate, IServerPacket
{
	
	private static byte [] message = "GOGO".getBytes(CharsetUtil.US_ASCII);
	
	public GoPacket() 
	{ 

	}

	@Override
	public void write( ByteBuf out )
	{
		out.writeBytes( message );
	}

}
