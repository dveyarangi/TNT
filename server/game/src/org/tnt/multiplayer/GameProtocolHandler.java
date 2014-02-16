package org.tnt.multiplayer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.util.ReferenceCountUtil;

import org.tnt.account.Character;
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
	
	private ChannelPipeline pipeline;
	private Channel channel;
	
	private volatile ChannelInboundHandler activeHandler;
	
	private MultiplayerOrchestrator orchestrator;
	
	private Player player;
	
	private AdminProtocolHandler adminHandler;
	
	public GameProtocolHandler ( Channel channel, ChannelPipeline pipeline, Player player, MultiplayerOrchestrator orchestrator)
	{
		this.pipeline = pipeline;
		this.channel = channel;
		
		this.orchestrator = orchestrator;
		
		this.player = player;
		
		switchToAdmin();
		
	}
	
	IngameProtocolHandler switchToRealTime(MultiplayerGame multiplayer, Character character) 
	{ 
		pipeline.remove( FRAME );
		pipeline.remove( AdminProtocolHandler.NAME );
		IngameProtocolHandler handler = new IngameProtocolHandler( channel, multiplayer, character );
		
		activeHandler = handler;
		
		
		pipeline.addLast( FRAME,                      new DelimiterBasedFrameDecoder( 2048, Delimiters.lineDelimiter() ) );
		pipeline.addLast( IngameProtocolHandler.NAME, handler );
		
		adminHandler = null;

		return handler;
	}
	
	AdminProtocolHandler switchToAdmin()    
	{
		pipeline.remove( FRAME );
		if(pipeline.get(IngameProtocolHandler.NAME) != null)
			pipeline.remove( IngameProtocolHandler.NAME );
		
		
		activeHandler = adminHandler = new AdminProtocolHandler( channel, orchestrator, player );
		
		pipeline.addLast( FRAME,                     new DelimiterBasedFrameDecoder( 2048, Delimiters.lineDelimiter() ) );
		pipeline.addLast( AdminProtocolHandler.NAME, adminHandler );
		
		return adminHandler;
	}
	
	AdminProtocolHandler getAdminHandler() { return adminHandler; }

	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
    	orchestrator.unregisterPlayerHandler( this.getPlayer() );
	}
	
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
    	try {
			activeHandler.channelRead( ctx, msg );
        } finally {
            ReferenceCountUtil.release(msg);
        }	        
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
    	activeHandler.exceptionCaught( ctx, cause ); 
    	
    	log.error( "Exception in game protocol", cause );
    }

	public Player getPlayer() { return player; }
    
}
