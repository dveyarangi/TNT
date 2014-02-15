/**
 * 
 */
package org.tnt.multiplayer.auth;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;

import org.tnt.account.Player;
import org.tnt.account.PlayerStore;
import org.tnt.multiplayer.GameProtocolHandler;
import org.tnt.multiplayer.MultiplayerOrchestrator;

import com.google.gson.Gson;
import com.spinn3r.log5j.Logger;

/**
 * This stateless handler is the only handler for a new client connection.
 * It authenticates the player (TODO) and if the auth is successful, replaces itself with
 * {@link GameProtocolHandler}
 * 
 * @author fimar
 */
@Sharable
public class AuthHandler extends ChannelInboundHandlerAdapter
{
	private final static Logger log = Logger.getLogger(AuthHandler.class);
	
	private final MultiplayerOrchestrator orchestrator;
	
	private final PlayerStore store;
	
	private static final Charset ENCODING = CharsetUtil.UTF_8;

	public static final String	NAME	= "auth";
	
	private Gson gson;
	
	
	private ByteBuf MSG_AUTH_FAILED = Unpooled.wrappedBuffer( "fail".getBytes() );
	private ByteBuf MSG_AUTH_SUCCED = Unpooled.wrappedBuffer( "ok".getBytes() );

	
	public AuthHandler(PlayerStore store, MultiplayerOrchestrator orchestrator)
	{
		this.store = store;
		
		this.orchestrator = orchestrator;
		
		this.gson = new Gson();
	}
	
	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
		log.debug( "Client channel is open and waiting for authentication: " + ctx.channel().toString() );
    }

	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
	}
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
    	Player credentials = null;
    	
    	ByteBuf buffer = (ByteBuf)msg;
    	
    	try {
    		// TODO: stream through reader instead:
    		String messageStr = buffer.toString( ENCODING );
    		MCAuth message = gson.fromJson( messageStr, MCAuth.class );
    		
    		// TODO: actual authentication!
    		credentials = store.getPlayer( message.getPlayerId() );
    		
    	}
    	catch(Exception e)
    	{
       		log.warn( "Auth failed", e);
			ctx.writeAndFlush( gson.toJson( MSAuthResult.FAILED_SERVER_ERROR ) );
			return;
    	}
       	finally 
       	{ 
       		ReferenceCountUtil.release(msg); 
       	}
    	
    	if(credentials == null)
		{
    		log.warn( "Auth failed.");
			ctx.writeAndFlush( gson.toJson( MSAuthResult.FAILED_UNKNOWN_PLAYER ) );
			return;
		}
    	
		log.debug( "Player %s was succesfully authenticated", credentials );
		ctx.writeAndFlush( gson.toJson( MSAuthResult.OK ) );

    	/////////////////////////////////////////////
    	// adding game handler with credentials info:
      	
    	// auth handler is no longer needed:
    	ctx.pipeline().remove( NAME );
    	
    	GameProtocolHandler handler = new GameProtocolHandler( ctx.channel(), ctx.pipeline(), credentials, orchestrator );
    	ctx.pipeline().addLast( handler );
      	
    	orchestrator.registerPlayerHandler( handler );

    }

}
