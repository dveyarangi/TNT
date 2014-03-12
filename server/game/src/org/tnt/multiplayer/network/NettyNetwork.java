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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.tnt.INetworkThread;
import org.tnt.IShutdownHook;
import org.tnt.config.NetworkConfig;
import org.tnt.multiplayer.network.auth.AuthHandler;
import org.tnt.multiplayer.network.auth.IAuthenticator;

import com.spinn3r.log5j.Logger;

@Singleton
public class NettyNetwork implements INetworkThread
{
	private final Logger log = Logger.getLogger(this.getClass());
	
	private final NetworkConfig config;
	
	private final IShutdownHook shutdownHook;
	
	private ServerBootstrap bootstrap;
	
	private ChannelFuture channel;
	
	private final IAuthenticator authenticator;
	
	@Inject
	public NettyNetwork(NetworkConfig config, final IAuthenticator authenticator, IShutdownHook shutdownHook)
	{
		this.config = config;
		this.authenticator = authenticator;
		this.shutdownHook = shutdownHook;
	}
	

	@Override
	public void init()
	{
		log.debug("Starting networking thread...");
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
		
		new Thread(this, "tnt-network").start();
		
//		Thread.sleep( 500 );
	}
	
	@Override
	public void run()
	{
		int port = config.getPort();
		try
		{
			// Bind and start to accept incoming connections.
			channel = bootstrap.bind( port ).sync();
			log.info( "Listening on port %d...", port );

			// Wait until the server socket is closed.
			// In this example, this does not happen, but you can do that to
			// gracefully
			// shut down your server.
			channel.channel().closeFuture().sync();
		}
		catch(Exception e)
		{
			log.fatal( "Failed to bind server channel on port %d: %s", port, e.getMessage());
			shutdownHook.fail();
			return;
		}
		
		safeStop();
	}


	@Override
	public void safeStop()
	{
		
		log.debug( "Shutting down networking thread..." );
		bootstrap.group().shutdownGracefully();
		log.debug( "Networking thread was shut down." );
		
	}

	
}
