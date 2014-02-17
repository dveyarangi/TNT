/**
 * 
 */
package org.tnt.multiplayer.auth;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;

import org.tnt.account.Player;
import org.tnt.account.PlayerStore;
import org.tnt.multiplayer.GameProtocolHandler;
import org.tnt.multiplayer.MultiplayerHub;

import com.google.gson.Gson;
import com.spinn3r.log5j.Logger;

/**
 * Client authentication handler.
 * 
 * This handler is the only handler for a new client connection.
 * 
 * It authenticates the player (TODO) and if the auth is successful, this handler replaces itself with
 * {@link GameProtocolHandler} to manage the rest of the protocol.
 * 
 * @author fimar
 */
@Sharable
public class AuthHandler extends ChannelInboundHandlerAdapter
{
	private final static Logger log = Logger.getLogger(AuthHandler.class);
	
	/**
	 * Multiplayer hub
	 */
	private final MultiplayerHub hub;
	
	/**
	 * Player store
	 */
	private final PlayerStore store;
	
	
	private static final Charset ENCODING = CharsetUtil.UTF_8;

	/**
	 * Auth handler name in the client's pipeline
	 */
	public static final String	NAME	= "auth";
	
	private Gson gson;
	
	public AuthHandler(PlayerStore store, MultiplayerHub hub)
	{
		this.store = store;
		
		this.hub = hub;
		
		this.gson = new Gson();
	}
	
	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
		log.trace( "Client channel is open and waiting for authentication: " + ctx.channel().toString() );
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
        	if(credentials == null)
    		{
        		log.warn( "Auth failed: unknown player (id " + message.getPlayerId() + ")");
        		writeAuthResult( ctx, MSAuthResult.FAILED_UNKNOWN_PLAYER );
    			return;
    		}
    		
    	}
    	catch(Exception e)
    	{
       		log.warn( "Auth failed", e);
       		writeAuthResult( ctx, MSAuthResult.FAILED_SERVER_ERROR );
			return;
    	}
       	finally 
       	{ 
       		ReferenceCountUtil.release(msg); 
       	}
    	

    	/////////////////////////////////////////////
    	// adding game handler with credentials info:
      	
   	
    	GameProtocolHandler handler = new GameProtocolHandler( ctx.channel(), ctx.pipeline(), credentials, hub );
     	
    	if(!hub.registerPlayerHandler( credentials, handler ))
    	{
       		log.warn( "Auth failed: player %s already logged in", credentials);
       		writeAuthResult( ctx, MSAuthResult.FAILED_ALREADY_LOGGED_IN);
       		return;
    	}    	

        	
   		log.debug( "Player %s was succesfully authenticated", credentials );
   		writeAuthResult( ctx,  MSAuthResult.OK );

   		// auth handler is no longer needed:
    	ctx.pipeline().remove( NAME );
    	ctx.pipeline().addLast( handler );

    }
    
    /**
     * Writes and flushes authentication outcome to the client.
     * 
     * @param ctx
     * @param result
     */
    private void writeAuthResult(ChannelHandlerContext ctx, MSAuthResult result)
    {
		ctx.writeAndFlush( gson.toJson( result ) + "\r\n" );
    }

}
