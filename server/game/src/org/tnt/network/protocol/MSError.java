package org.tnt.network.protocol;



public class MSError implements IServerMessage
{
	private final String error;
	
	MSError(String error)
	{
		this.error = error;
	}
}
