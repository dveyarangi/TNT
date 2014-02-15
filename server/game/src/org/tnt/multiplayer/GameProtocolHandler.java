package org.tnt.multiplayer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
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
	
	private ChannelPipeline pipeline;
	private Channel channel;
	
	private volatile ChannelInboundHandlerAdapter activeHandler;
	
	private MultiplayerOrchestrator orchestrator;
	
	private Player player;
	
	private AdminProtocolHandler adminHandler;
	
	public GameProtocolHandler ( Channel channel, ChannelPipeline pipeline, Player player, MultiplayerOrchestrator orchestrator)
	{
		this.pipeline = pipeline;
		this.channel = channel;
		
		this.orchestrator = orchestrator;
		
		this.player = player;
		
	}
	
	IngameProtocolHandler switchToRealTime(MultiplayerGame multiplayer, Character character) 
	{ 
		pipeline.remove( "frame" );
		pipeline.remove( AdminProtocolHandler.NAME );
		
		pipeline.addLast( "frame", IngameProtocolHandler.FRAME_DECODER );
		
		IngameProtocolHandler handler = new IngameProtocolHandler( channel, multiplayer, character );
		pipeline.addLast( IngameProtocolHandler.NAME, handler );
		
		adminHandler = null;

		return handler;
	}
	
	AdminProtocolHandler switchToAdmin()    
	{
		pipeline.remove( "frame" );
		pipeline.remove( IngameProtocolHandler.NAME );
		
		pipeline.addLast( "frame", AdminProtocolHandler.FRAME_DECODER );
		
		adminHandler = new AdminProtocolHandler( channel, orchestrator );
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
