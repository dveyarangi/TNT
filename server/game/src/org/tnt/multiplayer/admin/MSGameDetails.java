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
	
	private Player targetPlayer;
	
	private static class PlayerDetails {
		private long playerId;
		private long characterId;
		private long pid;
	}
	
	public MSGameDetails( Player targetPlayer, Map <Character, Integer> characterIds)
	{
		this.players = new LinkedList <> ();
		this.targetPlayer = targetPlayer;
		
		for( Character character : characterIds.keySet() )
		{
			PlayerDetails playerDetails = new PlayerDetails();
			playerDetails.playerId = character.getPlayer().getId();
			playerDetails.characterId = character.getId();
			playerDetails.pid = characterIds.get( character );
			players.add( playerDetails );
		}
	}

	@Override
	public Player getTargetPlayer()
	{
		return targetPlayer;
	}
	
}
