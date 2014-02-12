package org.tnt.multiplayer.admin;

import org.tnt.GameType;
import org.tnt.account.Player;
import org.tnt.multiplayer.MultiplayerOrchestrator;

public class MCGameRequest extends IClientMessage
{
	private GameType gameType;
	
	private int characterId;

	public GameType getGameType()
	{
		return gameType;
	}


	@Override
	void process( Player player, MultiplayerOrchestrator orchestrator )
	{
		orchestrator.addGameRequest( player, characterId, this );
	}
	
	
}
