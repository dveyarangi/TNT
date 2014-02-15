package org.tnt.multiplayer.auth;


/**
 * Initial authentication packet
 * TODO: should be something smarter
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
