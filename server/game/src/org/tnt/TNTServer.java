package org.tnt;

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

import org.tnt.account.PlayerStore;
import org.tnt.config.ServerConfig;
import org.tnt.config.TNTConfig;
import org.tnt.multiplayer.MultiplayerHub;
import org.tnt.multiplayer.auth.AuthHandler;

import com.spinn3r.log5j.Logger;

public class TNTServer 
{
	private Logger log = Logger.getLogger(this.getClass());

	private ServerConfig	config;
	
	private PlayerStore store;
	
	private MultiplayerHub orchestrator;
	
	private ChannelInitializer <SocketChannel> channelInitializer; 
	public static final DelimiterBasedFrameDecoder FRAME_DECODER = new DelimiterBasedFrameDecoder( 2048, Delimiters.lineDelimiter() );
	
	private ServerBootstrap bootstrap;
	
	public TNTServer(  )
	{

	}
	
	public void init( ServerConfig config )
	{
		log.info( "initializing..." );
		long startTime = System.currentTimeMillis();
		// server configuration
		this.config = config;
		
		// storage of player account, profile and characters
		this.store = new PlayerStore();
		
		// multiplayer hub:
		this.orchestrator = new MultiplayerHub(store);
		
		// authentication handler appendix:
		this.channelInitializer = new ChannelInitializer<SocketChannel>() {
			private AuthHandler handler = new AuthHandler( store, orchestrator );
			@Override public void initChannel( SocketChannel ch ) throws Exception
			{
				ch.pipeline().addLast( "frame", new DelimiterBasedFrameDecoder( 2048, Delimiters.lineDelimiter() ));
				ch.pipeline().addLast( "encoder", new StringEncoder());
				
				ch.pipeline().addLast( AuthHandler.NAME, handler );
			}
		};
		EventLoopGroup bossGroup   = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		bootstrap = new ServerBootstrap();
		bootstrap.group( bossGroup, workerGroup ).channel( NioServerSocketChannel.class );
		bootstrap.childHandler( channelInitializer );
		bootstrap.option( ChannelOption.SO_BACKLOG, 128 );
		bootstrap.childOption( ChannelOption.SO_KEEPALIVE, true );
		
		
		log.info( "Started " + (System.currentTimeMillis() - startTime) + " ms." );
	}

	public void run() throws Exception
	{
		try
		{
			// Bind and start to accept incoming connections.
			ChannelFuture f = bootstrap.bind( config.getPort() ).sync();
			log.info( "Listening on port %d...", config.getPort());

			// Wait until the server socket is closed.
			// In this example, this does not happen, but you can do that to
			// gracefully
			// shut down your server.
			f.channel().closeFuture().sync();
		}
		catch(Exception e)
		{
			log.fatal( "Failed to bind server channel on port %d: %s", config.getPort(), e.getMessage());
			System.exit(1);
		}
		finally
		{
			bootstrap.group().shutdownGracefully();
		}
	}

	public static void main( String[] args ) throws Exception
	{
		
		TNTConfig config = TNTConfig.load( args );

		TNTServer server = new TNTServer();
		
		server.init( config.getServerConfig() );
		
		server.run();
		
		TNTConsole console = new TNTConsole( server );
		Thread consoleThread = new Thread( console, "tnt-console" );
		consoleThread.setDaemon( true );
		consoleThread.start();
	}
}
