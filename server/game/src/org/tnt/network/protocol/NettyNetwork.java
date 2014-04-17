package org.tnt.network.protocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ThreadFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.tnt.IShutdownHook;
import org.tnt.config.NetworkConfig;

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

	private final static String NAME = "tnt-network";

	@Inject
	public NettyNetwork(final NetworkConfig config, final IAuthenticator authenticator, final IShutdownHook shutdownHook)
	{
		this.config = config;
		this.authenticator = authenticator;
		this.shutdownHook = shutdownHook;
	}


	@Override
	public void init()
	{
		log.debug("Starting networking thread...");

		ThreadFactory threadFactory = new DefaultThreadFactory(NAME);

		EventLoopGroup bossGroup   = new NioEventLoopGroup(0, threadFactory);
		EventLoopGroup workerGroup = new NioEventLoopGroup(0, threadFactory );

		bootstrap = new ServerBootstrap();
		bootstrap.group( bossGroup, workerGroup ).channel( NioServerSocketChannel.class );

		// using authenticator as initial channel handler:
		bootstrap.childHandler( authenticator );

		// TODO: understand this:
		bootstrap.option( ChannelOption.SO_BACKLOG, 128 );
		bootstrap.childOption( ChannelOption.SO_KEEPALIVE, true );

		new Thread(this, NAME).start();
	}

	@Override
	public void run()
	{
		int port = config.getPort();
		try
		{
			// Bind and start to accept incoming connections.
			channel = bootstrap.bind( port ).sync();
			log.info( "Ready to receive connections on port %d.", port );

			// Wait until the server socket is closed.
			// In this example, this does not happen, but you can do that to
			// gracefully
			// shut down your server.
			channel.channel().closeFuture().sync();
		}
		catch(Exception e)
		{
			log.fatal( "Failed to bind server channel on port %d: %s", port, e.getMessage());
			shutdownHook.fail( e );
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
