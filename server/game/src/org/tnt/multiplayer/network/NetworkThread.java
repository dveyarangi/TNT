package org.tnt.multiplayer.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;

import org.tnt.config.NetworkConfig;
import org.tnt.multiplayer.network.auth.AuthHandler;

import com.spinn3r.log5j.Logger;

public class NetworkThread extends Thread
{
	private final Logger log = Logger.getLogger(this.getClass());
	
	private final NetworkConfig config;
	
	private final ServerBootstrap bootstrap;
	
	private ChannelFuture channel;
	
	public NetworkThread(NetworkConfig config, final AuthHandler authenticator)
	{
		super("tnt-network");
		this.config = config;
		
		ChannelInitializer <SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
			@Override public void initChannel( final SocketChannel ch ) throws Exception
			{
				ch.pipeline().addLast( "frame", new DelimiterBasedFrameDecoder( 2048, Delimiters.lineDelimiter() ));
				ch.pipeline().addLast( "encoder", new StringEncoder());
				
				ch.pipeline().addLast( AuthHandler.NAME, authenticator );
/*				ch.closeFuture().addListener( new GenericFutureListener<ChannelFuture>() {
					@Override
					public void operationComplete( ChannelFuture future ) throws Exception
					{
						connectedChannels.remove( future.channel() );
					}
				} );*/
			}
		};
		EventLoopGroup bossGroup   = new NioEventLoopGroup(0, new DefaultThreadFactory("tnt-network"));
		EventLoopGroup workerGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("tnt-network") );
		bootstrap = new ServerBootstrap();
		bootstrap.group( bossGroup, workerGroup ).channel( NioServerSocketChannel.class );
		bootstrap.childHandler( channelInitializer );
		bootstrap.option( ChannelOption.SO_BACKLOG, 128 );
		bootstrap.childOption( ChannelOption.SO_KEEPALIVE, true );
		
		this.start();
	}
	
	
	@Override
	public void run()
	{
		try
		{
			// Bind and start to accept incoming connections.
			channel = bootstrap.bind( config.getPort() ).sync();
			log.info( "Listening on port %d...", config.getPort());

			// Wait until the server socket is closed.
			// In this example, this does not happen, but you can do that to
			// gracefully
			// shut down your server.
			channel.channel().closeFuture().sync();
		}
		catch(Exception e)
		{
			log.fatal( "Failed to bind server channel on port %d: %s", config.getPort(), e.getMessage());
		}
		finally
		{
			safeStop();
		}
	}


	public void safeStop()
	{
		
		log.debug( "Shutting down server socket..." );
		bootstrap.group().shutdownGracefully();
		
	}
	
}
