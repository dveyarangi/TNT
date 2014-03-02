package org.tnt.multiplayer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;

import org.tnt.account.Player;
import org.tnt.multiplayer.hub.HubProtocolHandler;
import org.tnt.multiplayer.hub.MSGameDetails;
import org.tnt.multiplayer.hub.MSGo;
import org.tnt.multiplayer.realtime.IngameProtocolHandler;

import com.spinn3r.log5j.Logger;
/**
 * This is game player driver that is directed by game client actions.
 * 
 * Game client actions are represented by 
 * 
 * This class creates and swaps between admin and in-game protocol handlers
 * 
 * @author Fima
 */
public class PlayerHubDriver extends ChannelInboundHandlerAdapter implements IPlayerDriver
{
	private static Logger log = Logger.getLogger(PlayerHubDriver.class);
	
	public static final String	FRAME	= "frame";
	
	/**
	 * Channel pipeline, that is managed by this handler.
	 */
	private final ChannelPipeline pipeline;
	
	/**
	 * Client communication channel
	 */
	private final Channel channel;
	
	/**
	 * Current protocol mode (admin/ingame)
	 */
	private volatile ChannelInboundHandler activeHandler;
	/**
	 * Admin protocol handler instance.
	 */
	private final HubProtocolHandler hubHandler;
	
	/**
	 * Multiplayer hub
	 */
	private final Hub hub;
	
	/**
	 * Player logged into this channel
	 */
	private final Player player;
	
	
	public PlayerHubDriver ( Channel channel, ChannelPipeline pipeline, Player player, Hub hub)
	{
		this.pipeline = pipeline;
		this.channel = channel;
		
		this.hub = hub;
		
		this.player = player;
		
		this.hubHandler = new HubProtocolHandler( channel, hub, player );
		
		switchToAdmin();
		
	}
	
	/**
	 * This method is invoked by game hub when player enters a game room or game room changes.
	 * 
	 */
	@Override
	public void gameRoomUpdated( GameRoom room )
	{
		// writing game room details to client:
		hubHandler.write( new MSGameDetails( room ) );
	}

	/**
	 * This methods is invoked by game hub when game starts.
	 * @return Handle to control behavior of in-game character this player controls.
	 */
	@Override
	public ICharacterDriver gameStarted(MultiplayerGame game, int pid)
	{
		// write game start to game room participant
		hubHandler.write( MSGo.GO );
		// switching to in-game protocol mode
		// from now until the end of the game, player communications 
		// are managed by {@link IngameProtocoHandler}
		return switchToRealTime( game, pid );
	}
	/**
	 * This methods is invoked by game hub when game ends.
	 */

	@Override
	public void gameEnded( IGameResults results )
	{
		// switching game to game hall protocol.
		switchToAdmin();
		
		hubHandler.write( results );
	}
	
	/**
	 * Reconfigures channel's pipeline for ingame protocol
	 * 
	 * @param multiplayer
	 * @param pid
	 * @return
	 */
	ICharacterDriver switchToRealTime(MultiplayerGame multiplayer, int pid) 
	{ 
		// removing administration protocol handlers:
		pipeline.remove( FRAME );
		pipeline.remove( HubProtocolHandler.NAME );
		
		
		// registering ingame protocol handler:
		IngameProtocolHandler handler = multiplayer.getPlugin().createNetworkCharacterDriver( channel, multiplayer, pid );
		
		activeHandler = handler;
		pipeline.addLast( IngameProtocolHandler.NAME, activeHandler );

		return handler;
	}
	
	/**
	 * Reconfigures channel's pipleline for administration protocol
	 * @return
	 */
	HubProtocolHandler switchToAdmin()    
	{
		// removing ingame protocol handler:
		pipeline.remove( FRAME );
		if(pipeline.get(IngameProtocolHandler.NAME) != null)
		{
			pipeline.remove( IngameProtocolHandler.NAME );
		}
		
		// registering administration protocol handlers:
		activeHandler = hubHandler;
		
		pipeline.addLast( FRAME,                     new DelimiterBasedFrameDecoder( 2048, Delimiters.lineDelimiter() ) );
		pipeline.addLast( HubProtocolHandler.NAME, hubHandler );
		
		return hubHandler;
	}

	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
		hub.unregisterPlayerHandler( player );
	}
	
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
		activeHandler.channelRead( ctx, msg );
    }
    

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
    	activeHandler.exceptionCaught( ctx, cause ); 
    	
    	log.error( "Exception in game protocol", cause );
    }

    
}
