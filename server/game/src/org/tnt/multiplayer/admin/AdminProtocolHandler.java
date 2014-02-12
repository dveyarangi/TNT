package org.tnt.multiplayer.admin;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;

import org.tnt.account.Player;
import org.tnt.multiplayer.GameProtocolHandler;
import org.tnt.multiplayer.MultiplayerOrchestrator;
import org.tnt.util.AbstractElementAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AdminProtocolHandler extends ChannelInboundHandlerAdapter 
{
	private boolean isActive = true;
	
	private final MultiplayerOrchestrator orchestrator;
	
	private Gson gson;
	
	private static final Charset ENCODING = CharsetUtil.UTF_8;
	
	private Player player;
	
	private GameProtocolHandler gameProtocolHandler;
	
	public AdminProtocolHandler(final MultiplayerOrchestrator orchestrator)
	{
		
		this.orchestrator = orchestrator;
		
		this.gson = new GsonBuilder()
			.registerTypeAdapter(IServerMessage.class, new AbstractElementAdapter<IServerMessage>())
			.create();
	}
	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
    	
    }

	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
		orchestrator.getPlayerRegistery().unregisterPlayer( ctx.channel() );
    }
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) 
    {
    	if(!isActive)
    		return;
    	ByteBuf buffer = (ByteBuf)msg;
    	
    	try {
    		// TODO: stream through reader instead:
    		String messageStr = buffer.toString( ENCODING );
    		IClientMessage message = gson.fromJson( messageStr, IClientMessage.class );
    		
    		if( message instanceof MCAuth )
    		{
    			MCAuth mauth = (MCAuth) message;
    			player = orchestrator.getPlayerRegistery().registerPlayer(mauth.getPlayerId(), ctx.channel());
    		}
    		else
    		if( message instanceof MCQuit )
    		{
    			orchestrator.getPlayerRegistery().unregisterPlayer( ctx.channel());
    		}
    		else
    		{
    			message.process( player, orchestrator );
    		}
 
        } 
    	finally { ReferenceCountUtil.release(msg); }	        
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) 
    {
    	if(!isActive)
    		return;
    	
//    	orchestrator.getPlayerRegistery().unregisterPlayer( ctx.channel() );
    	
        cause.printStackTrace();
        ctx.close();
    }		
}
