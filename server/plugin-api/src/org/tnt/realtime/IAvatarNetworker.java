package org.tnt.realtime;

import io.netty.channel.ChannelInboundHandler;

import org.tnt.multiplayer.IAvatarDriver;

public interface IAvatarNetworker extends IAvatarDriver, ChannelInboundHandler
{
	/**
	 * Handler's name for comm channel pipeline.
	 */
	public static final String	NAME	= "ingame";

}
