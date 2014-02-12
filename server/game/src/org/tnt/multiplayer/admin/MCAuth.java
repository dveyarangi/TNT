package org.tnt.multiplayer.admin;

import org.tnt.account.Player;
import org.tnt.multiplayer.MultiplayerOrchestrator;


public class MCAuth extends IClientMessage 
{
	private long playerId;
	
	public long getPlayerId() { return playerId; }

	@Override
	void process( Player player, MultiplayerOrchestrator orchestrator )
	{
		throw new UnsupportedOperationException( );
	}

}
