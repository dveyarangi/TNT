package org.tnt.test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import org.tnt.GameType;
import org.tnt.multiplayer.admin.MCGameRequest;
import org.tnt.multiplayer.admin.MSGameDetails;
import org.tnt.multiplayer.admin.MSGo;
import org.tnt.multiplayer.auth.MSAuthResult;
import org.tnt.multiplayer.realtime.GoPacket;

import com.google.gson.Gson;
import com.spinn3r.log5j.Logger;

public class ClientHandler extends ChannelInboundHandlerAdapter
{
	
	private Logger log = Logger.getLogger(this.getClass());
	
	private Gson gson = new Gson();

	
	private enum ClientState {
		STARTED, CONNECTED, GAME_REQUESTED, GAME_READY, INGAME
	}
	
	ClientState state = ClientState.STARTED;

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
		ctx.pipeline().addFirst( "string-encoder", new StringEncoder());
		ctx.pipeline().addFirst( "string-decoder", new StringDecoder());
		ctx.pipeline().addFirst( "frame", new DelimiterBasedFrameDecoder( 2048, Delimiters.lineDelimiter() ) );
   }
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
    	String jsonStr = (String) msg;
    	log.debug( "Got message from server: " + jsonStr );
    	
    	switch(state)
    	{
    	case STARTED:
    		if(! doAuth( jsonStr, ctx ) )
			{
    			ctx.close();
				return;
			}
    		
    	case CONNECTED:
    		doRequestGame( ctx );
    		break;
    	
    	case GAME_REQUESTED:
    		doInitGame( jsonStr, ctx );
    		break;

    	}
    }
    
    private void doAcknowledgeGame( String jsonStr, ChannelHandlerContext ctx )
	{
    	MSGo result = gson.fromJson( jsonStr, MSGo.class );
 		log.debug("Received game GO!: " + jsonStr);
 		
 		ctx.pipeline().remove( "frame" );
		ctx.pipeline().remove( "string-encoder" );
		ctx.pipeline().remove( "string-decoder" );
		ctx.pipeline().remove( this );
		IngameHandler handler = new IngameHandler(ctx.channel(), gameDetails);
		ctx.pipeline().addLast( "ingame", handler );
		
		handler.write( new GoPacket() );

		state = ClientState.GAME_READY;
		
	}

	private void doInitGame( String jsonStr, ChannelHandlerContext ctx )
	{
		try {
	    	gameDetails = gson.fromJson( jsonStr, MSGameDetails.class );
	  		log.debug("Received game details: " + jsonStr);
	   		
			state = ClientState.GAME_REQUESTED;
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
   		
		state = ClientState.GAME_REQUESTED;
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
   		
		state = ClientState.CONNECTED;
   	
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
