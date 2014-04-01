package org.tnt.network.realtime;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.tnt.multiplayer.IAvatar;
import org.tnt.multiplayer.IAvatarAction;
import org.tnt.multiplayer.IAvatarUpdate;

/**
 * This helper class manages in-game communication for a single client.
 * 
 * Game plugins may inherit
 * 
 * The class commu
 * @author fimar
 *
 */
public abstract class AvatarNetworker extends ChannelInboundHandlerAdapter implements IAvatarNetworker
{
	
	/**
	 * Multiplayer  this handler serves.
	 */
	private final IAvatar avatar;

	
	/**
	 * Defines ingame protocol states
	 */
	private enum GameState { STARTING, RUNNING, OVER };

	/**
	 * Current ingame protocol state.
	 */
	private GameState state = GameState.STARTING;
	
	/**
	 * Client communication channel.
	 */
	private final Channel channel;
	
	/**
	 * Outgoint messages buffer.
	 */
	private final ByteBuf outBuffer;
	
	public AvatarNetworker(Channel channel, IAvatar avatar)
	{
		this.channel = channel;
		
		this.avatar = avatar;
		
		outBuffer = Unpooled.wrappedBuffer( new byte [ getServerPacketSize() ] );
	}
	
	/**
	 * @return size of outgoing packets
	 */
	public abstract int getServerPacketSize();
	
	/**
	 * @return size of incoming packets
	 */
	public abstract int getClientPacketSize();
	
	/**
	 * Reads client message and updates the multiplayer game.
	 */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
    	ByteBuf buffer = (ByteBuf) msg;
    	if(buffer.readableBytes() < getClientPacketSize())
		 {
			return; // not yet arrived
		}
    	
    	switch(state)
    	{
    	case STARTING:
    		avatar.gameAcknowledged();
    	   	break;

    	case RUNNING:	
    		// TODO: decouple simulator and network threads?
    		avatar.putAction( parseClientUpdate( buffer ) );
    		break;
    	case OVER:
    		// TODO:
    		break;
    	}    	
    }

    /**
     * Transits the handler to running state and dispatches the initial client update.
     * 
     * @param update
     */
	@Override
	public void setStarted( IAvatarUpdate update ) 
    {
    	state = GameState.RUNNING;
    	update( update );
    }

	/**
	 * Convert client message to character action.
	 * @param buffer
	 * @return
	 */
    protected abstract IAvatarAction parseClientUpdate( ByteBuf buffer );
    
    /**
     * Sends the provided game update to the client. 
     * @param update
     */

	@Override
	public void update( IAvatarUpdate update )
	{
		// resetting output buffer:
		outBuffer.clear();
		outBuffer.setZero( 0, getServerPacketSize() );
		
		// writing message to buffer
		((IServerPacket)update).write( outBuffer );
		
		// padding buffer to fixed packet lenght:
		outBuffer.setIndex( 0, getServerPacketSize() );
		
		// sending buffer to client
		channel.writeAndFlush( outBuffer );
	}

	@Override
	public void stop()
	{
		// TODO Switch back to admin
		
	}
}
