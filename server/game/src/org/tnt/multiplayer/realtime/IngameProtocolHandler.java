package org.tnt.multiplayer.realtime;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.tnt.IGameUpdate;
import org.tnt.account.Character;
import org.tnt.multiplayer.ICharacterAction;
import org.tnt.multiplayer.MultiplayerGame;

public abstract class IngameProtocolHandler extends ChannelInboundHandlerAdapter 
{
//	public static final FixedLengthFrameDecoder FRAME_DECODER = new FixedLengthFrameDecoder( OUT_PACKET_SIZE );
	
	private ByteBuf outBuffer;
	
	private MultiplayerGame multiplayer;
	
	private int pid;
	
	private enum GameState { STARTING, RUNNING, OVER };
	
	private GameState state = GameState.STARTING;
	
	private Channel channel;
	
	public static final String	NAME	= "ingame";
	
//	private static final IGameUpdate GO_PACKET = new GoPacket();
	
	public IngameProtocolHandler(Channel channel, MultiplayerGame multiplayer, int pid)
	{
		this.channel = channel;
		this.multiplayer = multiplayer;
		this.pid = pid;
		
		outBuffer = Unpooled.wrappedBuffer( new byte [ getServerPacketSize() ] );
	}
	
	public abstract int getServerPacketSize();
	public abstract int getClientPacketSize();
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
    	ByteBuf buffer = (ByteBuf) msg;
    	if(buffer.readableBytes() < getClientPacketSize())
    		return; // not yet arrived
    	
    	switch(state)
    	{
    	case STARTING:
    	   	multiplayer.setGameAcknowledged( pid );
    	   	break;

    	case RUNNING:	
    		multiplayer.addCharacterAction( pid, parseClientUpdate( buffer ) );
    		break;
    	case OVER:
    		break;
    	}    	
    }

	public void setStarted( IGameUpdate update) 
    {
    	state = GameState.RUNNING;
    	write( update );
    }

    protected abstract ICharacterAction parseClientUpdate( ByteBuf buffer );
    
	public void write( IGameUpdate update )
	{
		outBuffer.clear();
		outBuffer.setZero( 0, getServerPacketSize() );
		
		update.write( outBuffer );
		outBuffer.setIndex( 0, getServerPacketSize() );
		
		channel.writeAndFlush( outBuffer );
	}

//	public MultiplayerGame getGame() { return multiplayer; }

	public void stop()
	{
		// TODO Auto-generated method stub
		
	}
}
