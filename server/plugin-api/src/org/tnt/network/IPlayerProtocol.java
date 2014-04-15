package org.tnt.network;

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;

public interface IPlayerProtocol extends ChannelInboundHandler, ChannelOutboundHandler{

	/**
	 * Sends client message to disconnect and terminates connection.
	 * 
	 * This is
	 * 
	 * @param serverShutdown
	 */
	void stop(MSClose close);

}
