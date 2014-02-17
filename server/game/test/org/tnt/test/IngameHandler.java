package org.tnt.test;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.tnt.multiplayer.IGameUpdate;
import org.tnt.multiplayer.admin.MSGameDetails;

import com.spinn3r.log5j.Logger;

public class IngameHandler extends ChannelInboundHandlerAdapter
{
	
	private Logger log = Logger.getLogger(this.getClass());
	
	private static final int	CLIENT_PACKET_SIZE	= 16;

	private static final int	SERVER_PACKET_SIZE	= 16;

	private ByteBuf outBuffer;
	
	private Channel channel;
	
	private MSGameDetails details;
	
	public IngameHandler(Channel channel, MSGameDetails details)
	{
		this.channel = channel;
		this.details = details;
		
		outBuffer = Unpooled.wrappedBuffer( new byte [ CLIENT_PACKET_SIZE ] );
	}
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
    	ByteBuf buffer = (ByteBuf) msg;
    	if(buffer.readableBytes() < SERVER_PACKET_SIZE)
		 {
			return; // not yet arrived
		}
    }



	public void write( IGameUpdate update )
	{
		outBuffer.clear();
		outBuffer.setZero( 0, CLIENT_PACKET_SIZE );
		
		update.write( outBuffer );
		outBuffer.setIndex( 0, CLIENT_PACKET_SIZE );
		
		channel.writeAndFlush( outBuffer );
	}
	
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
        ctx.close();
        log.info( "Disconnected from server" );
        System.exit( 0 );
    }

}
