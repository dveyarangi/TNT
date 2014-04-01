package org.tnt.multiplayer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;

import org.tnt.account.Player;
import org.tnt.multiplayer.network.hub.HubProtocolHandler;
import org.tnt.multiplayer.network.hub.MSClose;
import org.tnt.multiplayer.network.hub.MSGameDetails;
import org.tnt.multiplayer.network.hub.MSGo;
import org.tnt.multiplayer.realtime.Avatar;
import org.tnt.network.realtime.AvatarNetworker;
import org.tnt.network.realtime.IAvatarNetworker;
import org.tnt.plugins.IGameResults;

import com.spinn3r.log5j.Logger;
/**
 * This is game player driver that is directed by game client actions.
 * 
 * Game client actions are represented by 
 * 
 * This class creates and swaps between hub and in-game protocol handlers
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
	private HubProtocolHandler hubHandler;
	
	/**
	 * Multiplayer hub
	 */
	private final IHub hub;
	
	/**
	 * Player logged into this channel
	 */
	private final Player player;
	
	/**
	 * 
	 * @param channel
	 * @param pipeline
	 * @param player
	 * @param hub
	 */
	public PlayerHubDriver ( Channel channel, ChannelPipeline pipeline, Player player, IHub hub)
	{
		this.pipeline = pipeline;
		this.channel = channel;
		
		this.hub = hub;
		
		this.player = player;
		
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
	public IAvatarDriver playerInGame(GameRoom room, Avatar avatar)
	{
		// write game start to game room participant
		hubHandler.write( MSGo.GO );
		
		// switching to in-game protocol mode
		// from now until the end of the game, player communications 
		// are managed by {@link IngameProtocoHandler}
		// removing hub protocol handlers:
		pipeline.remove( FRAME );
		pipeline.remove( HubProtocolHandler.NAME );
		
		
		// registering ingame protocol handler:
		IAvatarNetworker handler = room.getPlugin().createAvatarNetworker( channel, avatar );
		
		activeHandler = handler;
		pipeline.addLast( IAvatarNetworker.NAME, activeHandler );

		return handler;
	}
	
	/**
	 * This methods is invoked by game hub when game ends.
	 */
	@Override
	public void gameEnded( IGameResults results )
	{
		
		hubHandler.write( results );
	}

	/**
	 * Reconfigures channel's pipeline for administration protocol
	 * @return
	 */
	@Override
	public void playerInHub()    
	{
		// removing ingame protocol handler:
		pipeline.remove( FRAME );
		if(pipeline.get(AvatarNetworker.NAME) != null)
		{
			pipeline.remove( AvatarNetworker.NAME );
		}
		
		// registering hub protocol handlers:
		if(hubHandler == null)
			hubHandler = new HubProtocolHandler( channel, hub, player );
		activeHandler = hubHandler;
		
		pipeline.addLast( FRAME,                     new DelimiterBasedFrameDecoder( 2048, Delimiters.lineDelimiter() ) );
		pipeline.addLast( HubProtocolHandler.NAME, hubHandler );
	}

	/**
	 * Called when client disconnection happens
	 * TODO: maybe use flag instead and a collection thread.
	 */
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
		hub.playerDisconnected( player );
	}
	
	/**
	 * Called when server reads something from client.
	 * 
	 * Just propagates the readout to current protocol handler, whether hub or ingame
	 */
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

	@Override
	public void stop(MSClose reason)
	{
		// write game start to game room participant
		hubHandler.write( reason );
		channel.close();
	}

    
}
