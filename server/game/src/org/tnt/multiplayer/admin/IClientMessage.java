package org.tnt.multiplayer.admin;

import org.tnt.account.Player;
import org.tnt.multiplayer.MultiplayerOrchestrator;

public abstract class IClientMessage
{
	
	private String type = this.getClass().getSimpleName().substring( 2 );
	
	public String getType() { return type; }
	
	public String toString() { return "ADMMSG: " + getType(); }

	abstract void process( Player player, MultiplayerOrchestrator orchestrator );
}
