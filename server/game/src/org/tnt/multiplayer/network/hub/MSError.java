package org.tnt.multiplayer.network.hub;

import org.tnt.network.hub.IServerMessage;

public class MSError implements IServerMessage
{
	private final String error;
	
	MSError(String error)
	{
		this.error = error;
	}
}
