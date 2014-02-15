package org.tnt;

import org.tnt.multiplayer.realtime.IServerPacket;

public interface IGameUpdate extends IServerPacket
{
	public int getPID();
}
