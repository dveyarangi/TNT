package org.tnt.multiplayer.admin;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;

import org.tnt.account.Player;
import org.tnt.multiplayer.MultiplayerHub;
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
public class AdminProtocolHandler extends ChannelInboundHandlerAdapter
{
	private final static Logger		log				= Logger.getLogger( AdminProtocolHandler.class );

	/**
	 * Handler name in netty pipeline
	 */
	public static final String		NAME			= "admin";

	/**
	 * Multiplayer service
	 */
	private final MultiplayerHub	orchestrator;

	/**
	 * Json encoder/decoder
	 */
	private Gson					inGson, outGson;

	/**
	 * Json string encoding
	 */
	private static final Charset	ENCODING		= CharsetUtil.UTF_8;

	/**
	 * Client messages autoresolve preffix. Used by {@link #inGson} to load
	 * messages into correct instance of {@link IClientMessage}
	 */
	private static final String		MESSAGE_PREFIX	= "org.tnt.multiplayer.admin.MC";

	/**
	 * Player that is managed by this handler.
	 */
	private Player					player;

	/**
	 * Network channel this handler manages.
	 */
	private Channel					channel;

	public AdminProtocolHandler( Channel channel, final MultiplayerHub orchestrator, Player player )
	{

		this.orchestrator = orchestrator;

		this.channel = channel;

		this.player = player;

		// inbound messages are parsed using custom type adapter that allows
		// loading concrete
		// instances of IClientMessage
		this.inGson = new GsonBuilder().registerTypeAdapter( IClientMessage.class,
				new AbstractElementAdapter<IClientMessage>( MESSAGE_PREFIX ) ).create();

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
		orchestrator.unregisterPlayerHandler( player );
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

			if( message instanceof MCQuit )
			{ // game quit message is sent when player decides to leave game
				// lobby or cancel matchfinding
				orchestrator.removeFromGame( player );
			}
			else
			{
				// executing message logic:
				message.process( player, orchestrator );
			}

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

		log.trace( "Writing to client " + player + " >>> " + jsonStr );

		// writing
		channel.writeAndFlush( jsonStr + "\r\n" );
	}

	@Override
	public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause )
	{
		// TODO: add some grace
		cause.printStackTrace();
		ctx.close();
	}

}
