package org.tnt.network.login;


/**
 * Initial authentication packet from client
 * TODO: should be something smarter, with SSL auth maybe
 * @author Fima
 *
 */
public class MCAuth
{
	private long playerId;
	
	public MCAuth(long playerId)
	{
		this.playerId = playerId;
	}
	
	public long getPlayerId() { return playerId; }

}
