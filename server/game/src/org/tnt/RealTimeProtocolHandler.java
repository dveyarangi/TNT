package org.tnt;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * This class manages the in-game communications with game client.
 * 
 * @author Fima
 */
public class RealTimeProtocolHandler extends ChannelInboundHandlerAdapter 
{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) 
    {
        try {
            // Do something with msg
        } finally {
            ReferenceCountUtil.release(msg);
        }	        // Discard the received data silently.
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) 
    {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }		
}
