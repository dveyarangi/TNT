package org.tnt.multiplayer.realtime;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.FixedLengthFrameDecoder;

import org.tnt.IGameUpdate;
import org.tnt.account.Character;
import org.tnt.multiplayer.MultiplayerGame;

public class IngameProtocolHandler extends ChannelInboundHandlerAdapter 
{
	private static final int IN_PACKET_SIZE = 128;
	private static final int OUT_PACKET_SIZE = 128;
	public static final FixedLengthFrameDecoder FRAME_DECODER = new FixedLengthFrameDecoder( OUT_PACKET_SIZE );
	
	private ByteBuf outBuffer;
	
	private MultiplayerGame multiplayer;
	private Character character;
	
	private enum GameState { INFORMING, STARTING, RUNNING, OVER };
	
	private GameState state;
	
	private Channel channel;
	
	public IngameProtocolHandler(Channel channel, MultiplayerGame multiplayer, Character character)
	{
		this.channel = channel;
		this.multiplayer = multiplayer;
		this.character = character;
		
		outBuffer = Unpooled.wrappedBuffer( new byte [ OUT_PACKET_SIZE ] );
	}
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
    	ByteBuf buffer = (ByteBuf) msg;
    	if(buffer.readableBytes() < IN_PACKET_SIZE)
    		return; // not yet arrived
    	
    	switch(state)
    	{
    	case INFORMING:
//    	   	multiplayer.addPlayerAcknowledgement();
    	   	break;
    	}    	
    }



	public void write( IGameUpdate update )
	{
		outBuffer.clear();
		
		update.write( outBuffer );
		
		channel.writeAndFlush( outBuffer );
	}

//	public MultiplayerGame getGame() { return multiplayer; }

	public void stop()
	{
		// TODO Auto-generated method stub
		
	}
}
