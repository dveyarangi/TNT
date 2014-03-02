package org.tnt.multiplayer.hub;

public class MSError implements IServerMessage
{
	private final String error;
	
	MSError(String error)
	{
		this.error = error;
	}
}
