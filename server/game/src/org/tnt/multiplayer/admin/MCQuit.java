package org.tnt.multiplayer.admin;

import org.tnt.account.Player;
import org.tnt.multiplayer.MultiplayerOrchestrator;

public class MCQuit extends IClientMessage
{

	@Override
	void process( Player player, MultiplayerOrchestrator orchestrator )
	{
		throw new UnsupportedOperationException( );
	}

}
