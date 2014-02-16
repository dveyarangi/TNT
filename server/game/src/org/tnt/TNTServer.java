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
import org.tnt.multiplayer.MultiplayerOrchestrator;
import org.tnt.multiplayer.auth.AuthHandler;

public class TNTServer 
{

	private ServerConfig	config;
	
	private PlayerStore store;
	
	private MultiplayerOrchestrator orchestrator;
	
	private ChannelInitializer <SocketChannel> channelInitializer; 
	public static final DelimiterBasedFrameDecoder FRAME_DECODER = new DelimiterBasedFrameDecoder( 2048, Delimiters.lineDelimiter() );
	

	public TNTServer( ServerConfig config )
	{
		// server configuration
		this.config = config;
		
		// storage of player account, profile and characters
		this.store = new PlayerStore();
		
		// multiplayer hub:
		this.orchestrator = new MultiplayerOrchestrator(store);
		
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
	}

	public void run() throws Exception
	{
		EventLoopGroup bossGroup   = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try
		{
			ServerBootstrap b = new ServerBootstrap();
			b.group( bossGroup, workerGroup ).channel( NioServerSocketChannel.class );
			b.childHandler( channelInitializer );
			b.option( ChannelOption.SO_BACKLOG, 128 );
			b.childOption( ChannelOption.SO_KEEPALIVE, true );

			// Bind and start to accept incoming connections.
			ChannelFuture f = b.bind( config.getPort() ).sync();

			// Wait until the server socket is closed.
			// In this example, this does not happen, but you can do that to
			// gracefully
			// shut down your server.
			f.channel().closeFuture().sync();
		}
		finally
		{
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	public static void main( String[] args ) throws Exception
	{
		TNTConfig config = TNTConfig.load( args );

		new TNTServer( config.getServerConfig() ).run();
	}
}
