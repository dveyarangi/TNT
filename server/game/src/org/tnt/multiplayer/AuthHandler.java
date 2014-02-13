/**
 * 
 */
package org.tnt.multiplayer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;

import org.tnt.account.Player;
import org.tnt.account.PlayerStore;
import org.tnt.multiplayer.admin.MCAuth;

import com.google.gson.Gson;
import com.spinn3r.log5j.Logger;

/**
 * This stateless handler is the only handler for a new client connection.
 * It authenticates the player (TODO) and if the auth is successful, replaces itself with
 * {@link GameProtocolHandler}
 * 
 * @author fimar
 */
public class AuthHandler extends ChannelInboundHandlerAdapter
{
	private final static Logger log = Logger.getLogger(GameProtocolHandler.class);
	
	private final MultiplayerOrchestrator orchestrator;
	
	private final PlayerStore store;
	
	private static final Charset ENCODING = CharsetUtil.UTF_8;
	
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
			ctx.writeAndFlush( MSG_AUTH_FAILED );
    	}
       	finally { ReferenceCountUtil.release(msg); }
    	
    	if(credentials == null)
		{
    		log.warn( "Auth failed.");
			ctx.writeAndFlush( MSG_AUTH_FAILED );
			return;
		}

    	/////////////////////////////////////////////
    	// adding game handler with credentials info:
    	
    	GameProtocolHandler handler = new GameProtocolHandler( orchestrator, credentials );
    	ctx.pipeline().addLast( handler );
    	
    	// auth handler is no longer needed:
    	ctx.pipeline().remove( this );
    	
    }

}
