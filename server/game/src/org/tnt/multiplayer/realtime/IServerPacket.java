package org.tnt.multiplayer.realtime;

import io.netty.buffer.ByteBuf;

public interface IServerPacket 
{
	public void write(ByteBuf out);
	public int getPacketSize();

}
