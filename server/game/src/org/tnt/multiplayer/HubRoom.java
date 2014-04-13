/**
 * 
 */
package org.tnt.multiplayer;

import java.util.ArrayList;
import java.util.List;

import org.tnt.account.Player;
import org.tnt.util.IDProvider;

/**
 * Represents a room that several players may be in simultaneously.
 * 
 * Possible implementations are: p2p chat channel, group chat, game lobby
 * 
 * @author fimar
 */
public class HubRoom 
{
	
	/**
	 * Room creation time
	 * TODO: use to monitor rooms that are pending for too long.
	 */
	private final long creationTime = System.currentTimeMillis();
	
	/**
	 * Game system-unique identifier for persistence and reference.
	 */
	private final String roomId;

	/**
	 * joined characters
	 */
	private final List <Player> players;

	/**
	 * 
	 */
	public HubRoom()
	{
		super();
		this.roomId = IDProvider.generateGameId();
		this.players = new ArrayList <> ();
	}

	public String getGameId() {	return roomId; }
	
	public void addParticipant( Player player ) { players.add( player ); }
	public void removeParticipant( Player player ) { players.remove( player ); }

	/**
	 * Retrieves participant list.
	 * Order of participants in this list determines character's short ingame identifier
	 * @return
	 */
	public List <Player> getParticipants() { return players; }

}