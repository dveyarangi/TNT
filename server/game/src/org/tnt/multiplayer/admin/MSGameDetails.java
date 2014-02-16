package org.tnt.multiplayer.admin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tnt.account.Player;
import org.tnt.multiplayer.GameRoom;
import org.tnt.account.Character;


@SuppressWarnings( "unused" )
public class MSGameDetails implements IServerMessage
{
	private List <PlayerDetails> players;

	
	private static class PlayerDetails {
		private long playerId;
		private long characterId;
		private int pid;
		private GameData gameData;
		
		public static class GameData {
			private int trackNumber;
		}
	}
	
	public MSGameDetails( List<Character> list)
	{
		this.players = new LinkedList <> ();
		
		int idx = 0;
		for( Character character : list )
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
