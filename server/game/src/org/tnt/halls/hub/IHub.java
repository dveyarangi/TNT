package org.tnt.halls.hub;

import org.tnt.account.Player;
import org.tnt.game.IGameResults;
import org.tnt.halls.realtime.Arena;
import org.tnt.multiplayer.network.hub.HubException;

public interface IHub
{
//	public boolean playerConnected( Player player, IPlayerHubDriver driver );

	void gameOver( Arena arena, IGameResults results );

	void init();

//	void playerDisconnected( Player player );

	void removeFromGameRoom( Player player );

	void addGameRequest( Player player, int charId, String gameType ) throws HubException;

	void safeStop();

}
