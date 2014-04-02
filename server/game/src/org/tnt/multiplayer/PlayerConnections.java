package org.tnt.multiplayer;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import org.tnt.account.Player;
import org.tnt.multiplayer.network.hub.MSClose;

@Singleton
public class PlayerConnections implements IPlayerConnections
{

	
	/**
	 * List of players currently available in the hub.
	 * Each player has a corresponding player driver, to receive or send messages to player controller
	 */
	private final Map <Player, IPlayerHubDriver> activePlayers = new HashMap <> ();

	@Override
	public boolean hasPlayer( Player player )
	{
		return activePlayers.containsKey( player );
	}

	@Override
	public void putPlayer( Player player, IPlayerHubDriver driver )
	{
		activePlayers.put( player, driver );
	}

	@Override
	public void removePlayer( Player player )
	{
		activePlayers.remove( player );
	}

	@Override
	public IPlayerHubDriver getPlayerDriver( Player player )
	{
		return activePlayers.get( player );
	}

	@Override
	public void safeStop()
	{
//		log.debug( "Disconnecting from pending clients (%d total)...", activePlayers.size() );
		// dropping players from rooms:
		for(IPlayerHubDriver driver : activePlayers.values())
			driver.stop(MSClose.SERVER_SHUTDOWN);
			
	}	

}
