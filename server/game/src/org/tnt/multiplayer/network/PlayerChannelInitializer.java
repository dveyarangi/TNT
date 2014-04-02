package org.tnt.multiplayer.network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringEncoder;

import javax.inject.Inject;

import org.tnt.multiplayer.network.auth.AuthHandler;
import org.tnt.multiplayer.network.auth.IAuthenticator;

public class PlayerChannelInitializer extends ChannelInitializer <SocketChannel>
{
	
	private final IAuthenticator authenticator;
	
	
	@Inject
	PlayerChannelInitializer(IAuthenticator authenticator)
	{
		this.authenticator = authenticator;
	}
	
	@Override public void initChannel( final SocketChannel ch ) throws Exception
	{
		ch.pipeline().addLast( "frame", new DelimiterBasedFrameDecoder( 2048, Delimiters.lineDelimiter() ));
		ch.pipeline().addLast( "encoder", new StringEncoder());
		
		ch.pipeline().addLast( AuthHandler.NAME, authenticator );

	}
}
