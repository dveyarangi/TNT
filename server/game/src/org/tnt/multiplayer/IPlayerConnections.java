package org.tnt.multiplayer;

import org.tnt.account.Player;

public interface IPlayerConnections
{

	boolean hasPlayer( Player player );

	void putPlayer( Player player, IPlayerHubDriver driver );

	void removePlayer( Player player );

	IPlayerHubDriver getPlayerDriver( Player player );

	void safeStop();

}
