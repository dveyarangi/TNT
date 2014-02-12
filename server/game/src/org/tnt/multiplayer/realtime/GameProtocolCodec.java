package org.tnt.multiplayer.realtime;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.List;

import org.tnt.account.Character;
import org.tnt.multiplayer.MultiplayerGame;

public class GameProtocolCodec extends ChannelInboundHandlerAdapter 
{
	private static final int IN_PACKET_SIZE = 128;
	private static final int OUT_PACKET_SIZE = 128;
	
	private MultiplayerGame multiplayer;
	private Character character;
	
	private enum GameState { INFORMING, STARTING, RUNNING, OVER };
	
	private GameState state;
	
	public GameProtocolCodec()
	{
	}
	
	public void activate(MultiplayerGame multiplayer, Character character)
	{
		this.state = GameState.INFORMING;
		this.multiplayer = multiplayer;
		this.character = character;
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
	private static class Encoder extends MessageToByteEncoder<ServerPacket>
	{

		@Override
		protected void encode( ChannelHandlerContext ctx, ServerPacket msg, ByteBuf out )
				throws Exception
		{
			out.writeByte( msg.playerId );
			out.writeInt( msg.time );
			out.writeFloat( msg.x );
			out.writeFloat( msg.y );
			out.writeByte( msg.action.ordinal() );
		}
	}
	
	private static class Decoder extends ByteToMessageDecoder
	{
		@Override
		protected void decode( ChannelHandlerContext ctx, ByteBuf msg, List<Object> out )
				throws Exception
		{
			// TODO Auto-generated method stub
			
		}
	}
}
