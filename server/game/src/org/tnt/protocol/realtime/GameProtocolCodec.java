package org.tnt.protocol.realtime;

import java.util.List;

import org.ietf.jgss.MessageProp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;

public class GameProtocolCodec
{

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
