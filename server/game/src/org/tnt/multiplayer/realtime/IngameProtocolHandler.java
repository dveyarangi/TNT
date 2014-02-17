package org.tnt.multiplayer.realtime;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.tnt.multiplayer.ICharacterAction;
import org.tnt.multiplayer.IGameUpdate;
import org.tnt.multiplayer.MultiplayerGame;

/**
 * This class manages in-game fast protocol for a single client.
 * @author fimar
 *
 */
public abstract class IngameProtocolHandler extends ChannelInboundHandlerAdapter 
{
	/**
	 * Handler's name for comm channel pipeline.
	 */
	public static final String	NAME	= "ingame";
	
	/**
	 * Multiplayer game this handler serves.
	 */
	private MultiplayerGame multiplayer;
	
	/** 
	 * Character short id (determined by character join order to the game room).
	 */
	private int pid;
	
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
	private Channel channel;
	
	/**
	 * Outgoint messages buffer.
	 */
	private ByteBuf outBuffer;
	
	public IngameProtocolHandler(Channel channel, MultiplayerGame multiplayer, int pid)
	{
		this.channel = channel;
		this.multiplayer = multiplayer;
		this.pid = pid;
		
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
    	   	multiplayer.setGameAcknowledged( pid );
    	   	break;

    	case RUNNING:	
    		multiplayer.addCharacterAction( pid, parseClientUpdate( buffer ) );
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
	public void setStarted( IGameUpdate update) 
    {
    	state = GameState.RUNNING;
    	write( update );
    }

	/**
	 * Convert client message to character action.
	 * @param buffer
	 * @return
	 */
    protected abstract ICharacterAction parseClientUpdate( ByteBuf buffer );
    
    /**
     * Sends the provided game update to the client. 
     * @param update
     */
	public void write( IGameUpdate update )
	{
		// resetting output buffer:
		outBuffer.clear();
		outBuffer.setZero( 0, getServerPacketSize() );
		
		// writing message to buffer
		update.write( outBuffer );
		
		// padding buffer to fixed packet lenght:
		outBuffer.setIndex( 0, getServerPacketSize() );
		
		// sending buffer to client
		channel.writeAndFlush( outBuffer );
	}

	public void stop()
	{
		// TODO Switch back to admin
		
	}
}
