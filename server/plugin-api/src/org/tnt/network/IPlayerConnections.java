package org.tnt.network;

import io.netty.channel.Channel;

import org.tnt.account.IPlayer;

public interface IPlayerConnections
{

	boolean hasPlayer( IPlayer player );

	public IPlayerProtocol playerConnected(final IPlayer player, final Channel channel);
	public void disconnectPlayer( final IPlayer player, final MSClose reason );

	public void assessConnections();

	void safeStop();

}
