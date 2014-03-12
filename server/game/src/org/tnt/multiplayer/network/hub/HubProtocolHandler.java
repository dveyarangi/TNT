package org.tnt.multiplayer.network.hub;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;

import org.tnt.account.Player;
import org.tnt.multiplayer.IHub;
import org.tnt.util.AbstractElementAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spinn3r.log5j.Logger;

/**
 * Handler for player/character management and game setup protocol (for all
 * non-realtime stuff, actually).
 * 
 * @author fimar
 */
public class HubProtocolHandler extends ChannelInboundHandlerAdapter
{
	private final static Logger		log				= Logger.getLogger( HubProtocolHandler.class );

	/**
	 * Handler name in netty pipeline
	 */
	public static final String		NAME			= "admin";

	/**
	 * Multiplayer service
	 */
	private final IHub	hub;

	/**
	 * Json encoder/decoder
	 */
	private final Gson					inGson, outGson;

	/**
	 * Json string encoding
	 */
	private static final Charset	ENCODING		= CharsetUtil.UTF_8;

	private static final String		MESSAGE_TYPE_NAME	= "type";
	/**
	 * Client messages autoresolve preffix. Used by {@link #inGson} to load
	 * messages into correct instance of {@link IClientMessage}
	 */
	private static final String		MESSAGE_TYPE_PREFIX	= "org.tnt.multiplayer.network.hub.MC";
	private static final String		MESSAGE_TYPE_SUFFIX	= "";

	/**
	 * Player that is managed by this handler.
	 */
	private final Player					player;

	/**
	 * Network channel this handler manages.
	 */
	private final Channel					channel;

	public HubProtocolHandler( Channel channel, final IHub hub, Player player )
	{

		this.hub = hub;

		this.channel = channel;

		this.player = player;

		// inbound messages are parsed using custom type adapter that allows
		// loading concrete
		// instances of IClientMessage
		this.inGson = new GsonBuilder().registerTypeAdapter( IClientMessage.class,
				new AbstractElementAdapter<IClientMessage>( MESSAGE_TYPE_NAME, MESSAGE_TYPE_PREFIX, MESSAGE_TYPE_SUFFIX ) ).create();

		// outbound messages are parsed by default gson parser
		this.outGson = new GsonBuilder().create();
	}

	@Override
	public void channelActive( ChannelHandlerContext ctx ) throws Exception
	{
		// TODO: check out
	}

	@Override
	public void channelInactive( ChannelHandlerContext ctx ) throws Exception
	{
		// dropping player from the orchestrator:
		hub.playerDisconnected( player );
	}

	/**
	 * Reads incoming client JSON messages and executes their corresponding
	 * methods.
	 * 
	 * @param ctx
	 * @param msg
	 */
	@Override
	public void channelRead( ChannelHandlerContext ctx, Object msg )
	{
		// we are using raw bytebuf channel:
		ByteBuf buffer = (ByteBuf) msg;

		try
		{
			// TODO: stream through reader instead:
			String jsonStr = buffer.toString( ENCODING );

			log.trace( "Reading from client " + player + " <<< " + jsonStr );

			// we will get a concrete message instance here:
			IClientMessage message = inGson.fromJson( jsonStr, IClientMessage.class );


			// executing message logic:
			message.process( player, hub );

		}
		catch( HubException e )
		{
			writeError( e );
		}
		finally
		{
			ReferenceCountUtil.release( msg );
		}
	}

	/**
	 * Writes server message to the client.
	 * 
	 * @param message
	 */
	public void write( IServerMessage message )
	{
		// serializing message into JSON format:
		String jsonStr = outGson.toJson( message );

		log.trace( "Writing to client [" + player + "] >>> " + jsonStr );

		// writing
		channel.writeAndFlush( jsonStr + "\r\n" );
	}

	@Override
	public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause )
	{
		writeError( cause );
		ctx.close();
	}
	/**
	 * Writes server message to the client.
	 * 
	 * @param message
	 */
	private void writeError( Throwable cause )
	{
		// serializing message into JSON format:
		String jsonStr = outGson.toJson( new MSError( cause.getMessage() ) );

		log.trace( "Writing to client [" + player + "] >>> " + jsonStr );
		log.error( "Error in client communication.", cause );

		// writing
		channel.writeAndFlush( jsonStr + "\r\n" );
	}

}
