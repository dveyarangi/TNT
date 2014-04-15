package org.tnt.network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

import javax.inject.Inject;

import org.tnt.network.auth.AuthHandler;
import org.tnt.network.auth.IAuthenticator;

public class DummyChannelInitializer extends ChannelInitializer <SocketChannel>
{

	private final IAuthenticator authenticator;


	@Inject
	DummyChannelInitializer(final IAuthenticator authenticator)
	{
		this.authenticator = authenticator;
	}

	@Override public void initChannel( final SocketChannel ch ) throws Exception
	{
		ch.pipeline().addLast( AuthHandler.NAME, authenticator );

	}
}
