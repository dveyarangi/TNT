package org.tnt.multiplayer;

import org.tnt.account.Player;
import org.tnt.multiplayer.network.hub.HubException;
import org.tnt.multiplayer.realtime.Arena;
import org.tnt.plugins.IGameResults;

public interface IHub
{
	public boolean playerConnected( Player player, IPlayerDriver driver );

	void gameOver( Arena arena, IGameResults results );

	void init();

	void playerDisconnected( Player player );

	void removeFromGameRoom( Player player );

	void addGameRequest( Player player, int charId, String gameType ) throws HubException;

	void safeStop();

}
