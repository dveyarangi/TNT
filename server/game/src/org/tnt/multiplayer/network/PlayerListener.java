package org.tnt.multiplayer.network;

import org.tnt.account.Player;
import org.tnt.multiplayer.IPlayerHubDriver;

public interface PlayerListener
{

	public boolean playerConnected( Player player, IPlayerHubDriver driver );
	public void playerDisconnected( Player player );

}
