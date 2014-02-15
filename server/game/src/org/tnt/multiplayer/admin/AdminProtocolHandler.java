package org.tnt.multiplayer.admin;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;

import org.tnt.account.Player;
import org.tnt.multiplayer.MultiplayerOrchestrator;
import org.tnt.multiplayer.auth.AuthHandler;
import org.tnt.util.AbstractElementAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spinn3r.log5j.Logger;

public class AdminProtocolHandler extends ChannelInboundHandlerAdapter 
{
	private final static Logger log = Logger.getLogger(AuthHandler.class);
	
	public static final DelimiterBasedFrameDecoder FRAME_DECODER = new DelimiterBasedFrameDecoder( 2048, Delimiters.lineDelimiter() );
	
	
	private final MultiplayerOrchestrator orchestrator;
	
	private Gson gson;
	
	private static final Charset ENCODING = CharsetUtil.UTF_8;

	public static final String	NAME	= "admin";
	
	private Player player;
	
	private Channel channel;
	
	public AdminProtocolHandler(Channel channel, final MultiplayerOrchestrator orchestrator)
	{
		
		this.orchestrator = orchestrator;
		
		this.channel = channel;
		
		this.gson = new GsonBuilder()
			.registerTypeAdapter(IClientMessage.class, new AbstractElementAdapter<IClientMessage>())
			.create();
	}
	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
    	
    }

	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
		orchestrator.unregisterPlayerHandler( player );
    }
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) 
    {
    	ByteBuf buffer = (ByteBuf)msg;
    	
    	try {
    		// TODO: stream through reader instead:
    		String jsonStr = buffer.toString( ENCODING );
        	log.debug( "Reading from client " + player + " >>> " + jsonStr );
        	IClientMessage message = gson.fromJson( jsonStr, IClientMessage.class );
    		

    		if( message instanceof MCQuit )
    		{
    			orchestrator.removeFromGame( player );
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
    	
        cause.printStackTrace();
        ctx.close();
    }
    
    public void write(IServerMessage message)
    {
    	String jsonStr = gson.toJson( message );
    	
    	log.debug( "Writing to client " + player + " >>> " + jsonStr );
    	
    	byte [] bytes = jsonStr.getBytes();
    	
    	ByteBuf buffer = Unpooled.wrappedBuffer( bytes );
    	channel.write( buffer );
    }
 }
