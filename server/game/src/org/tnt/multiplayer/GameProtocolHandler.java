package org.tnt.multiplayer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;

import org.tnt.account.Player;
import org.tnt.multiplayer.admin.AdminProtocolHandler;
import org.tnt.multiplayer.realtime.IngameProtocolHandler;

import com.spinn3r.log5j.Logger;
/**
 * This class creates and swaps between admin and ingame protocol handlers
 * 
 * @author Fima
 */
public class GameProtocolHandler extends ChannelInboundHandlerAdapter 
{
	private static Logger log = Logger.getLogger(GameProtocolHandler.class);
	
	public static final String	FRAME	= "frame";
	
	/**
	 * Channel pipeline, that is managed by this handler.
	 */
	private ChannelPipeline pipeline;
	
	/**
	 * Client communication channel
	 */
	private Channel channel;
	
	/**
	 * Current protocol mode (admin/ingame)
	 */
	private volatile ChannelInboundHandler activeHandler;
	/**
	 * Admin protocol handler instance.
	 */
	private AdminProtocolHandler adminHandler;
	
	/**
	 * Multiplayer hub
	 */
	private MultiplayerHub hub;
	
	/**
	 * Player logged into this channel
	 */
	private Player player;
	
	
	public GameProtocolHandler ( Channel channel, ChannelPipeline pipeline, Player player, MultiplayerHub hub)
	{
		this.pipeline = pipeline;
		this.channel = channel;
		
		this.hub = hub;
		
		this.player = player;
		
		switchToAdmin();
		
	}
	
	/**
	 * Reconfigures channel's pipeline for ingame protocol
	 * 
	 * @param multiplayer
	 * @param pid
	 * @return
	 */
	IngameProtocolHandler switchToRealTime(MultiplayerGame multiplayer, int pid) 
	{ 
		// removing administration protocol handlers:
		pipeline.remove( FRAME );
		pipeline.remove( AdminProtocolHandler.NAME );
		
		
		// registering ingame protocol handler:
		IngameProtocolHandler handler = hub.createHandler( channel, multiplayer, pid );
		
		activeHandler = handler;
		pipeline.addLast( IngameProtocolHandler.NAME, handler );
		
		adminHandler = null;

		return handler;
	}
	
	/**
	 * Reconfigures channel's pipleline for administration protocol
	 * @return
	 */
	AdminProtocolHandler switchToAdmin()    
	{
		// removing ingame protocol handler:
		pipeline.remove( FRAME );
		if(pipeline.get(IngameProtocolHandler.NAME) != null)
		{
			pipeline.remove( IngameProtocolHandler.NAME );
		}
		
		
		// registering administration protocol handlers:
		activeHandler = adminHandler = new AdminProtocolHandler( channel, hub, player );
		
		pipeline.addLast( FRAME,                     new DelimiterBasedFrameDecoder( 2048, Delimiters.lineDelimiter() ) );
		pipeline.addLast( AdminProtocolHandler.NAME, adminHandler );
		
		return adminHandler;
	}
	AdminProtocolHandler getAdminHandler() { return adminHandler; }

	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
		hub.unregisterPlayerHandler( player );
	}
	
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
		activeHandler.channelRead( ctx, msg );
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
    	activeHandler.exceptionCaught( ctx, cause ); 
    	
    	log.error( "Exception in game protocol", cause );
    }
    
}
