package org.tnt.multiplayer;

import java.util.HashMap;
import java.util.Map;

import org.tnt.account.Player;
import org.tnt.account.PlayerStore;

import io.netty.channel.Channel;

public class PlayersRegistry
{
	private PlayerStore store;
	
	private Map <Channel, Player> playerByChannel = new HashMap <Channel, Player> ();
	private Map <Player, Channel> channelByPlayer = new HashMap <Player, Channel> ();
	private Map <Channel, GameProtocolHandler> gameHandlers = new HashMap <Channel, GameProtocolHandler> ();
	
	public PlayersRegistry(PlayerStore store)
	{
		this.store = store;
	}
	
	public void registerChannel(Channel channel, GameProtocolHandler gameHandler)
	{
		gameHandlers.put( channel, gameHandler );
	}
	public void unregisterChannel(Channel channel)
	{
		gameHandlers.remove( channel );
	}
	
 	public Player registerPlayer( long playerId, Channel channel )
	{
 		Player player = store.getPlayer( playerId );
 		if(player == null)
 			return null;
 		
 		playerByChannel.put( channel, player );
 		channelByPlayer.put( player, channel );
 		
		return player;
	}


	public void unregisterPlayer( Channel channel )
	{
		channelByPlayer.remove( playerByChannel.remove( channel ) );
	}

	public Player getPlayer( Channel channel )
	{
		return playerByChannel.get( channel );
	}

	public Channel getChannel( Player player )
	{
		return channelByPlayer.get( player );
	}

	public GameProtocolHandler getPlayerHandler(Player player)
	{
		Channel channel = channelByPlayer.get( player );
		return gameHandlers.get( channel );
	}

}
