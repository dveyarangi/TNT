/**
 * 
 */
package org.tnt.multiplayer;

import java.util.ArrayList;
import java.util.List;

import org.tnt.account.Player;
import org.tnt.util.IDProvider;

/**
 * @author fimar
 *
 */
public class HubRoom
{
	/**
	 * Game system-unique identifier for persistence and reference.
	 */
	private final String roomId;

	/**
	 * joined characters
	 */
	final List <Player> players = new ArrayList <> ();

	/**
	 * 
	 */
	public HubRoom()
	{
		super();
		this.roomId = IDProvider.generateGameId();
	}

	public String getGameId() {	return roomId; }
	
	public void addParticipant( Player player )
	{
		players.add( player );
	}

	public void removeParticipant( Player player )
	{
		players.remove( player );
	}

	/**
	 * Retrieves participant list.
	 * Order of participants in this list determines character's short ingame identifier
	 * @return
	 */
	public List <Player> getParticipants() { return players; }

}