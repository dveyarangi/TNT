package org.tnt.halls.hub;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;

import org.tnt.account.Player;
import org.tnt.game.IGameResults;
import org.tnt.halls.realtime.Avatar;
import org.tnt.multiplayer.IAvatarDriver;
import org.tnt.network.realtime.AvatarNetworker;
import org.tnt.network.realtime.IAvatarNetworker;

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
public class PlayerHubDriver extends ChannelInboundHandlerAdapter implements IPlayerHubDriver, IHubRoomSerializer
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
	public PlayerHubDriver ( final Channel channel, final ChannelPipeline pipeline, final Player player, final IHub hub)
	{
		this.pipeline = pipeline;
		this.channel = channel;

		this.hub = hub;

		this.player = player;

		this.hubSerializer = new JsonHubSerializer();
	}

	/**
	 * This method is invoked by game hub when player enters a game room or game room changes.
	 * 
	 */
	@Override
	public void roomUpdated( final HubRoom room )
	{
		// writing game room details to client:
		room.write( this );
	}

	/**
	 * This methods is invoked by game hub when game starts.
	 * @return Handle to control behavior of in-game character this player controls.
	 */
	@Override
	public IAvatarDriver playerInGame(final GameRoom room, final Avatar avatar)
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
	public void gameEnded( final IGameResults results )
	{
		playerInHub();

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
		if(hubHandler == null) {
			hubHandler = new HubProtocolHandler( channel, hub, player );
		}
		activeHandler = hubHandler;

		pipeline.addLast( FRAME,                     new DelimiterBasedFrameDecoder( 2048, Delimiters.lineDelimiter() ) );
		pipeline.addLast( HubProtocolHandler.NAME, hubHandler );
	}

	@Override
	public Player getPlayer() { return player; }

	@Override
	public void serialize(final GameRoom room) {

	}


}
