package org.tnt.network.protocol;

import org.tnt.account.Player;

public interface PlayerListener
{

	public boolean playerConnected( Player player );
	public void playerDisconnected( Player player );

}
