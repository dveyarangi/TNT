package org.tnt.multiplayer.network.hub;

import java.util.LinkedList;
import java.util.List;

import org.tnt.account.Character;
import org.tnt.multiplayer.GameRoom;
import org.tnt.network.hub.IServerMessage;

/**
 * This message is sent by server to inform client about his game room.
 * This message may be sent several times, as game room settings and participants change.
 * 
 * @author fimar
 */
@SuppressWarnings( "unused" )
public class MSGameDetails implements IServerMessage
{
	/** 
	 * Game id
	 */
	final String id;
	
	/**
	 * List of participating players and their ingame details
	 */
	final List <PlayerDetails> players;

	/**
	 * Player's game participation aspect
	 */
	private static class PlayerDetails {
		private long playerId;
		private long characterId;
		private int pid;
		private GameData gameData;
		
		public static class GameData {
			private int trackNumber;
		}
	}
	
	/**
	 * Creates a new game details message from list of participating characters. 
	 * Each character receives a short room id according to his ordinal in this list.
	 */
	public MSGameDetails( GameRoom room)
	{
		this.id = room.getGameId();
		
		this.players = new LinkedList <> ();
		
		
		int idx = 0;
		for( Character character : room.getCharacters() )
		{
			PlayerDetails playerDetails = new PlayerDetails();
			playerDetails.playerId = character.getPlayer().getId();
			playerDetails.characterId = character.getId();
			playerDetails.pid = idx;
			playerDetails.gameData = new PlayerDetails.GameData();
			playerDetails.gameData.trackNumber = playerDetails.pid;
			players.add( playerDetails );
			
			idx ++;
		}
	}
}
