package org.tnt.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import javax.inject.Inject;

import org.tnt.account.IPlayer;
import org.tnt.halls.IHall;
import org.tnt.halls.IHalls;

import com.google.gson.Gson;
import com.google.inject.assistedinject.Assisted;
import com.spinn3r.log5j.Logger;

/**
 * This class redirects client messages from a player's channel to the addressed {@link Hall}
 * @author Fima
 *
 */
public class PlayerProtocol extends ChannelDuplexHandler implements IPlayerProtocol
{
	private Logger log = Logger.getLogger(this.getClass());

	private IPlayer player;

	private Channel channel;

	private IHalls halls;

	private Gson outGson;

	private boolean isActive = false;

	@Inject public PlayerProtocol(
			@Assisted final IPlayer player,
			@Assisted final Channel channel,
			final IHalls halls)
	{
		this.player = player;
		this.channel = channel;
		this.halls = halls;

		this.outGson = new Gson();
	}


	/**
	 * Called when client disconnection happens
	 * TODO: maybe use flag instead and a collection thread.
	 */
	@Override
	public void channelInactive(final ChannelHandlerContext ctx) throws Exception
	{
		isActive = false;
	}

	/**
	 * Called when server reads something from client.
	 * 
	 * Just propagates the readout to current protocol handler, whether hub or ingame
	 */
	@Override
	public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception
	{
		ByteBuf buffer = (ByteBuf) msg;

		int hallId = buffer.readByte();

		IHall hall = halls.getHall( hallId);

		hall.read( buffer );
	}


	@Override
	public void stop(final MSClose reason)
	{
		// write game start to game room participant
		write( 0, reason );
		channel.close();
	}

	public void write(final int hallId, final Object message)
	{

		channel.write( hallId );
		channel.write( message );

		channel.flush();
	}

	@Override
	public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
		ctx.write(msg, promise);
	}

	@Override
	public void exceptionCaught( final ChannelHandlerContext ctx, final Throwable cause )
	{
		writeError( cause );
	}
	/**
	 * Writes server message to the client.
	 * 
	 * @param message
	 */
	private void writeError( final Throwable cause )
	{
		// serializing message into JSON format:
		String jsonStr = outGson.toJson( new MSError( cause.getMessage() ) );

		log.trace( "Writing to client [" + player + "] >>> " + jsonStr );
		log.error( "Error in client communication.", cause );

		// writing
		channel.writeAndFlush( jsonStr + "\r\n" );
	}

}
