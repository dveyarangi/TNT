package org.tnt.multiplayer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import org.tnt.multiplayer.admin.AdminProtocolHandler;
import org.tnt.multiplayer.realtime.GameProtocolCodec;
import org.tnt.account.Character;
/**
 * This class manages the in-game communications with game client.
 * 
 * @author Fima
 */
public class GameProtocolHandler extends ChannelInboundHandlerAdapter 
{
	
	private AdminProtocolHandler adminHandler;
	private GameProtocolCodec gameHandler;
	
	private volatile ChannelInboundHandlerAdapter activeHandler;
	
	private MultiplayerOrchestrator orchestrator;
	
	public GameProtocolHandler (MultiplayerOrchestrator orchestrator)
	{
		this.orchestrator = orchestrator;
		
		activeHandler = adminHandler = new AdminProtocolHandler( orchestrator );
		gameHandler = new GameProtocolCodec(  );
		
	}
	
	public void switchToRealTime(MultiplayerGame multiplayer, Character character) 
	{ 
		gameHandler.activate( multiplayer, character );
		this.activeHandler = gameHandler; 
	}
	
	public void switchToAdmin()    
	{
		
		this.activeHandler = adminHandler; 
	}
	
	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
		orchestrator.getPlayerRegistery().registerChannel( ctx.channel(), this );
    }

	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
		adminHandler.channelInactive( ctx );
		gameHandler.channelInactive( ctx );
		
		orchestrator.getPlayerRegistery().unregisterChannel( ctx.channel() );
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
    }	
    
    
}
