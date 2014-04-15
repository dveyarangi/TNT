package org.tnt.network;

import io.netty.channel.Channel;

import org.tnt.account.IPlayer;

public interface IProtocolFactory
{
	public IPlayerProtocol create(IPlayer player, Channel channel);
}
