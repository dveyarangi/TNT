package org.tnt.protocol;

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
    		ctx.write(msg); 
    	    ctx.flush(); 
        } finally {
            ReferenceCountUtil.release(msg);
        }	        
    	
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) 
    {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }		
}
