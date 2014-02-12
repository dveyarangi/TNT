package org.tnt.multiplayer.admin;

import org.tnt.account.Player;
import org.tnt.multiplayer.MultiplayerOrchestrator;

public class MGo extends IClientMessage
{

	@Override
	void process( Player player, MultiplayerOrchestrator orchestrator )
	{
		orchestrator.getGame( player ).gameAcknowledged();
	}

}
