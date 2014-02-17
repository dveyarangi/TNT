package org.tnt.multiplayer.realtime;

import io.netty.buffer.ByteBuf;

/**
 * Interface for messages sent by server
 * @author fimar
 *
 */
public interface IServerPacket 
{
	/**
	 * Serializes this message into the provided buffer.
	 * 
	 * @param out
	 */
	public void write(ByteBuf out);
}
