package org.tnt.multiplayer.realtime;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

import org.tnt.IGameUpdate;

public class GoPacket implements IGameUpdate
{
	
	private static byte [] message = "GOGO".getBytes(CharsetUtil.US_ASCII);
	
	private int pid;
	
	public GoPacket(int pid) 
	{ 
		this.pid = pid; 
	}

	@Override
	public void write( ByteBuf out )
	{
		out.writeBytes( message );
	}

	@Override
	public int getPacketSize() { return 4; }

	@Override
	public int getPID() { return pid; }

}
