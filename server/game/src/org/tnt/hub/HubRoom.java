/**
 * 
 */
package org.tnt.hub;

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
	private final List <IPlayerHubDriver> players;

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
	
	public void addParticipant( Player player, IPlayerHubDriver driver ) 
	{ 
		players.add( player );
		
		// sending game details message:
		for(Player participant : gameroom.getParticipants())
		{
			IPlayerHubDriver driver = connections.getPlayerDriver( participant );
			
			driver.gameRoomUpdated( this );
		}		
	}
	public void removeParticipant( Player player ) { players.remove( player ); }

	/**
	 * Retrieves participant list.
	 * Order of participants in this list determines character's short ingame identifier
	 * @return
	 */
	public List <Player> getParticipants() { return players; }

	public int getPopulationSize() { return players.size(); }

	public void write(PlayerHubDriver playerHubDriver) {
		// TODO Auto-generated method stub
		
	}

}