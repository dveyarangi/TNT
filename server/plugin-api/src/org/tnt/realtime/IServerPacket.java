package org.tnt.realtime;

import org.tnt.multiplayer.IAvatarUpdate;

import io.netty.buffer.ByteBuf;

/**
 * Interface for messages sent by server
 * @author fimar
 *
 */
public interface IServerPacket extends IAvatarUpdate
{
	/**
	 * Serializes this message into the provided buffer.
	 * 
	 * @param out
	 */
	public void write(ByteBuf out);
}
