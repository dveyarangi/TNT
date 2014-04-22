package org.tnt.network.protocol;

import io.netty.channel.Channel;

import javax.inject.Inject;

import org.tnt.account.IPlayer;
import org.tnt.network.codec.ICodecMap;

public class ProtocolFactory implements IProtocolFactory
{
	private final final ICodecMap serializers;

	@Inject public ProtocolFactory(final ICodecMap serializers)
	{
		this.serializers = serializers;
	}

	@Override
	public IPlayerProtocol create(final IPlayer player, final Channel channel)
	{
		return new PlayerProtocol(player, channel, serializers);
	}

}
