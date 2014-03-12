package org.tnt.multiplayer;

import org.tnt.account.Player;

public interface IPlayerConnections
{

	boolean hasPlayer( Player player );

	void putPlayer( Player player, IPlayerDriver driver );

	void removePlayer( Player player );

	IPlayerDriver getPlayerDriver( Player player );

	void safeStop();

}
