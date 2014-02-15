package org.tnt.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetAddress;

public class Client
{
	private EventLoopGroup workerGroup;
	
	private ChannelFuture future;
	
	public Client(int port)
	{
		workerGroup = new NioEventLoopGroup();
		try
		{
			Bootstrap b = new Bootstrap(); // (1)
			b.group( workerGroup ); // (2)
			b.channel( NioSocketChannel.class ); // (3)
			b.option( ChannelOption.SO_KEEPALIVE, true ); // (4)
			b.handler( new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel( SocketChannel ch ) throws Exception
				{
					ch.pipeline().addLast( new StringEncoder());
					ch.pipeline().addLast( new ClientHandler() );
				}
			} );
	
			// Start the client.
			future = b.connect( InetAddress.getLocalHost(), port ).sync(); // (5)
	
			// Wait until the connection is closed.
		}
		catch(Exception e)
		{
			e.printStackTrace();
			future.channel().close();
			workerGroup.shutdownGracefully();
		}

	}
	
	public Channel getChannel()
	{
		return future.channel();
	}
	
	public void stop()
	{
		future.channel().close();
		workerGroup.shutdownGracefully();
	}
}
