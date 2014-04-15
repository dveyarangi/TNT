package org.tnt.network;

import io.netty.channel.Channel;

import javax.inject.Inject;

import org.tnt.IHalls;
import org.tnt.account.IPlayer;

public class ProtocolFactory implements IProtocolFactory
{
	private final IHalls halls;

	@Inject public ProtocolFactory(final IHalls halls)
	{
		this.halls = halls;
	}

	@Override
	public IPlayerProtocol create(final IPlayer player, final Channel channel)
	{
		return new PlayerProtocol(player, channel, halls);
	}

}
