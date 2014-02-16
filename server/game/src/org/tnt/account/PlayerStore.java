package org.tnt.account;

import java.util.HashMap;
import java.util.Map;



public class PlayerStore
{
	private Map <Long, Player> players = new HashMap <Long, Player> ();
		
	public PlayerStore()
	{
		Player player1 = new Player(1);
		player1.getCharacters().add( new Character( 0, player1 ) );
		players.put( player1.getId(), player1 );
		Player player2 = new Player(2);
		player2.getCharacters().add( new Character( 0, player2) );
		players.put( player2.getId(), player2 );
	}

	public Player getPlayer( long playerId )
	{
		
		return players.get( playerId );
	}

}
