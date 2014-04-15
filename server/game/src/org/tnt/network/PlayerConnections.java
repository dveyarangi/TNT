package org.tnt.network;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import org.tnt.account.IPlayer;

import com.google.inject.Inject;
import com.spinn3r.log5j.Logger;

/**
 * Manages map of player netty channels,
 * TODO: player kick method {@link #disconnectPlayer(IPlayer, MSClose)} should be extracted to management api
 * 
 * @author Fima
 */
@Singleton
public final class PlayerConnections implements IPlayerConnections
{

	/**
	 * A logger
	 */
	private final Logger log = Logger.getLogger(this.getClass());


	private final IProtocolFactory protocolFactory;

	/**
	 * List of players currently available in the hub.
	 * Each player has a corresponding player driver, to receive or send messages to player controller
	 */
	private final Map <IPlayer, IPlayerProtocol> activePlayers = new HashMap <> ();

	@Inject
	private PlayerConnections(final IProtocolFactory protocolFactory)
	{
		this.protocolFactory = protocolFactory;
	}

	@Override
	public IPlayerProtocol playerConnected(final IPlayer player, final Channel channel)
	{

		if(hasPlayer( player ))
			throw new IllegalArgumentException("Player " + player + " is already connected" +
					"(registered object: " + activePlayers.get( player ) +")");

		IPlayerProtocol protocol = protocolFactory.create(player, channel);

		activePlayers.put( player, protocol );

		log.debug("Player " + player + " has connected to server using " + protocol);

		return protocol;
	}


	/**
	 * @param player
	 */
	@Override
	public void disconnectPlayer( final IPlayer player, final MSClose reason )
	{
		if(!hasPlayer( player ))
			throw new IllegalArgumentException("Player " + player + " is already disconnected.");

		IPlayerProtocol protocol = activePlayers.remove( player );

		protocol.stop( reason );
	}

	//	@Override
	public IPlayerProtocol getPlayerProtocol( final IPlayer player )
	{
		return activePlayers.get( player );
	}

	@Override
	public void safeStop()
	{
		log.debug( "Disconnecting from active clients (%d total)...", activePlayers.size() );
		// dropping players from rooms:
		for(IPlayerProtocol driver : activePlayers.values()) {
			driver.stop(MSClose.SERVER_SHUTDOWN);
		}

	}
	@Override
	public boolean hasPlayer(final IPlayer player)
	{
		return activePlayers.containsKey( player );
	}

	@Override
	public void assessConnections() {
		// TODO Auto-generated method stub

	}

}
