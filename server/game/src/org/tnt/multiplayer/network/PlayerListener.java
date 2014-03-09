package org.tnt.multiplayer.network;

import org.tnt.account.Player;
import org.tnt.multiplayer.IPlayerDriver;

public interface PlayerListener
{

	public boolean playerConnected( Player player, IPlayerDriver driver );
	public void playerDisconnected( Player player );

}
