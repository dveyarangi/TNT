/**
 * 
 */
package org.tnt.network.login;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.tnt.account.IPlayer;
import org.tnt.account.IPlayerStore;
import org.tnt.hub.PlayerHubDriver;
import org.tnt.network.protocol.IAuthenticator;
import org.tnt.network.protocol.IPlayerConnections;
import org.tnt.network.protocol.IPlayerProtocol;

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
@Singleton
public class AuthHandler extends  ChannelInitializer <SocketChannel> implements IAuthenticator
{
	private final static Logger log = Logger.getLogger(AuthHandler.class);

	/**
	 * Multiplayer hub
	 */
	private final IPlayerConnections playerConnections;

	/**
	 * Player store
	 */
	private final IPlayerStore store;


	private static final Charset ENCODING = CharsetUtil.UTF_8;

	/**
	 * Auth handler name in the client's pipeline
	 */
	public static final String	NAME	= "auth";

	private final Gson gson;

	@Inject
	public AuthHandler( final IPlayerConnections playerConnections, final IPlayerStore store)
	{
		this.playerConnections = playerConnections;
		this.store = store;
		this.gson = new Gson();
	}

	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception
	{

		ctx.pipeline().addLast( "frame", new DelimiterBasedFrameDecoder( 2048, Delimiters.lineDelimiter() ));
		ctx.pipeline().addLast( "encoder", new StringEncoder());

		log.trace( "Client channel is open and waiting for authentication: %s", ctx.channel().toString() );
	}

	@Override
	public void channelInactive(final ChannelHandlerContext ctx) throws Exception
	{
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception
	{
		IPlayer credentials = null;

		ByteBuf buffer = (ByteBuf)msg;

		try {
			// TODO: stream through reader instead:
			String messageStr = buffer.toString( ENCODING );
			MCAuth message = gson.fromJson( messageStr, MCAuth.class );

			// TODO: actual authentication!
			credentials = doAuthenticate( message );
			if(credentials == null)
			{
				log.warn( "Auth failed: unknown player (id %d)", message.getPlayerId());
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


		IPlayerProtocol protocol = playerConnections.playerConnected( credentials, ctx.channel() );
		if(protocol == null)
		{
			log.warn( "Auth failed: player %s already logged in", credentials);
			writeAuthResult( ctx, MSAuthResult.FAILED_ALREADY_LOGGED_IN);
			return;
		}

		writeAuthResult( ctx,  MSAuthResult.OK );

		// auth handler is no longer needed:
		ctx.pipeline().remove( this );
		ctx.pipeline().addLast( protocol );

	}

	private IPlayer doAuthenticate( final MCAuth message )
	{
		IPlayer credentials = store.getPlayer( message.getPlayerId() );

		return credentials;
	}

	/**
	 * Writes and flushes authentication outcome to the client.
	 * 
	 * @param ctx
	 * @param result
	 */
	private void writeAuthResult(final ChannelHandlerContext ctx, final MSAuthResult result)
	{
		ctx.writeAndFlush( gson.toJson( result ) + "\r\n" );
		if(! result.isOk()) // terminating failed auth connection
		{
			log.debug( "Auth failed: terminating client connection." );
			ctx.close();
		}
	}

	@Override
	protected void initChannel(final SocketChannel ch) throws Exception {
		// NOOP
	}

}
