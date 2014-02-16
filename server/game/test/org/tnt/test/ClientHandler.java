package org.tnt.test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.tnt.multiplayer.auth.MSAuthResult;

import com.google.gson.Gson;
import com.spinn3r.log5j.Logger;

public class ClientHandler extends ChannelInboundHandlerAdapter
{
	
	private Logger log = Logger.getLogger(this.getClass());
	
	private Gson gson = new Gson();
	
	private boolean isAuthed = false;
	
	@Override
    public void channelActive(final ChannelHandlerContext ctx) {
    }
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
    	String jsonStr = (String) msg;
    	log.debug( "Got message from server: " + jsonStr );
    	
    	if(! isAuthed)
    	{
    		if(! doAuth( jsonStr, ctx ) )
			{
				return;
			}
    	}
    }
    
    /**
	 * @param ctx
	 * @return
	 */
	private boolean doAuth( String jsonStr, ChannelHandlerContext ctx )
	{
    	MSAuthResult result = gson.fromJson( jsonStr, MSAuthResult.class );
    	
    	if(!result.isOk())
    	{
    		log.error( "Auth failed: " + result );
    		ctx.close();
    		return false;
    	}
    	
    	return true;
	}

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
        ctx.close();
        log.info( "Disconnected from server" );
        System.exit( 0 );
    }

}
