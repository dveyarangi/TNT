package org.tnt.test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.tnt.GameType;
import org.tnt.multiplayer.admin.MCGameRequest;
import org.tnt.multiplayer.admin.MSGameDetails;
import org.tnt.multiplayer.admin.MSGo;
import org.tnt.multiplayer.auth.MSAuthResult;

import com.google.gson.Gson;
import com.spinn3r.log5j.Logger;

public class ClientHandler extends ChannelInboundHandlerAdapter
{
	
	private Logger log = Logger.getLogger(this.getClass());
	
	private Gson gson = new Gson();
	
	private boolean isAuthed = false;
	private boolean isGameRequested = false;
	private boolean isGameInited = false;
	private boolean isGameAcknowledged = false;

	private int	playerId;

	private int	charId;
	
	private MSGameDetails gameDetails;
	
	public ClientHandler(int playerId, int charId)
	{
		this.playerId = playerId;
		this.charId = charId;
	}
	
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
    			ctx.close();
				return;
			}
     	}
    	
    	if(! isGameRequested)
    	{
    		doRequestGame( ctx );
    		
    		return;
    	}
    	
    	if(! isGameAcknowledged)
    	{
    		doInitGame( jsonStr, ctx );
    		return;
    	}

    	
    }
    
    private void doAcknowledgeGame( String jsonStr, ChannelHandlerContext ctx )
	{
    	MSGo result = gson.fromJson( jsonStr, MSGo.class );
 		log.debug("Received game GO!: " + jsonStr);
 		 		
		isGameAcknowledged = true;
		
	}

	private void doInitGame( String jsonStr, ChannelHandlerContext ctx )
	{
		try {
	    	gameDetails = gson.fromJson( jsonStr, MSGameDetails.class );
	  		log.debug("Received game details: " + jsonStr);
	   		
			isGameInited = true;
		}
		catch(Exception e) // TODO: that is bad, but i am lazy
		{
			doAcknowledgeGame( jsonStr, ctx);
		}
		
	}

	private void doRequestGame( ChannelHandlerContext ctx )
	{
		MCGameRequest request = new MCGameRequest( GameType.RAT_RACE, charId );
		
		String jsonStr = gson.toJson( request );
		log.debug( "Sending game request:" + jsonStr );
		ctx.writeAndFlush( jsonStr + "\r\n" );
   		
 		isGameRequested = true;
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
   		
		isAuthed = true;
   	
   		log.debug("Successfully logged into server.");
   	  		    	
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
