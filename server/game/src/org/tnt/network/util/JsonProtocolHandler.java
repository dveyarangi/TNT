package org.tnt.network.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;

import org.tnt.account.IPlayer;
import org.tnt.network.protocol.IClientMessage;
import org.tnt.network.protocol.IServerMessage;
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
public class JsonProtocolHandler extends ChannelInboundHandlerAdapter
{
	private final static Logger		log				= Logger.getLogger( JsonProtocolHandler.class );

	/**
	 * Handler name in netty pipeline
	 */
	public static final String		NAME			= "admin";


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
	private final IPlayer					player;

	/**
	 * Network channel this handler manages.
	 */
	private final Channel					channel;

	//TODO: make hall's protocol handdlers not able to control
	public JsonProtocolHandler( final Channel channel, final IPlayer player )
	{

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

	/**
	 * Reads incoming client JSON messages and executes their corresponding
	 * methods.
	 * 
	 * @param ctx
	 * @param msg
	 */
	@Override
	public void channelRead( final ChannelHandlerContext ctx, final Object msg )
	{
		// we are using raw bytebuf channel:
		ByteBuf buffer = (ByteBuf) msg;

		// TODO: stream through reader instead:
		String jsonStr = buffer.toString( ENCODING );

		log.trace( "Reading from client " + player + " <<< " + jsonStr );

		// we will get a concrete message instance here:
		IClientMessage message = inGson.fromJson( jsonStr, IClientMessage.class );


		// executing message logic:
		// TODO: messages as actions
		//		message.process( player, hub );

		ReferenceCountUtil.release( msg );

	}

	/**
	 * Writes server message to the client.
	 * 
	 * @param message
	 */
	public void write( final IServerMessage message )
	{
		// serializing message into JSON format:
		String jsonStr = outGson.toJson( message );

		log.trace( "Writing to client [" + player + "] >>> " + jsonStr );

		// writing
		channel.writeAndFlush( jsonStr + "\r\n" );
	}

}
