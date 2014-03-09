/**
 * 
 */
package org.tnt.multiplayer.network.auth;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;

import org.tnt.account.Player;
import org.tnt.account.PlayerStore;
import org.tnt.multiplayer.PlayerHubDriver;
import org.tnt.multiplayer.network.PlayerListener;

import com.google.gson.Gson;
import com.spinn3r.log5j.Logger;

/**
 * Client authentication handler.
 * 
 * This handler is the only handler for a new client connection.
 * 
 * It authenticates the player (TODO) and if the auth is successful, this handler replaces itself with
 * {@link PlayerHubDriver} to manage the rest of the game client communications.
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
	private final PlayerListener listener;
	
	/**
	 * Player store
	 */
	private final PlayerStore store;
	
	
	private static final Charset ENCODING = CharsetUtil.UTF_8;

	/**
	 * Auth handler name in the client's pipeline
	 */
	public static final String	NAME	= "auth";
	
	private final Gson gson;
	
	public AuthHandler(PlayerStore store, PlayerListener listener)
	{
		this.store = store;
		
		this.listener = listener;
		
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
    		credentials = doAuthenticate( message );
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
    	
    	
		log.debug( "Player %s was succesfully authenticated", credentials );

    	/////////////////////////////////////////////
    	// adding game handler with credentials info:
     	
   	
    	PlayerHubDriver handler = new PlayerHubDriver( ctx.channel(), ctx.pipeline(), credentials, listener );
     	
    	if(!listener.playerConnected( credentials, handler ))
    	{
       		log.warn( "Auth failed: player %s already logged in", credentials);
       		writeAuthResult( ctx, MSAuthResult.FAILED_ALREADY_LOGGED_IN);
       		return;
    	}    	

   		writeAuthResult( ctx,  MSAuthResult.OK );

   		// auth handler is no longer needed:
    	ctx.pipeline().remove( NAME );
    	ctx.pipeline().addLast( handler );

    }
    
    private Player doAuthenticate( MCAuth message )
	{
    	Player credentials = store.getPlayer( message.getPlayerId() );
   	
    	return credentials;
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
		if(! result.isOk()) // terminating failed auth connection
		{
       		log.debug( "Auth failed: terminating client connection." );
			ctx.close();
		}
    }

}
