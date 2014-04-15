package org.tnt.account;

import java.util.HashMap;
import java.util.Map;



public class PlayerStore implements IPlayerStore
{
	private final Map <Long, IPlayer> players = new HashMap <Long, IPlayer> ();

	public PlayerStore()
	{
	}

	@Override
	public void init()
	{
		IPlayer player1 = new Player(1);
		player1.addCharacter( new Character( 0, player1 ) );
		players.put( player1.getId(), player1 );
		IPlayer player2 = new Player(2);
		player2.addCharacter( new Character( 0, player2) );
		players.put( player2.getId(), player2 );
	}

	@Override
	public IPlayer getPlayer( final long playerId )
	{

		return players.get( playerId );
	}

}
