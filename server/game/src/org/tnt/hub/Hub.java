package org.tnt.hub;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.tnt.account.Character;
import org.tnt.account.Player;
import org.tnt.game.IGameFactory;
import org.tnt.game.IGamePlugin;
import org.tnt.game.IGameResults;
import org.tnt.network.IPlayerConnections;
import org.tnt.realtime.Arena;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.spinn3r.log5j.Logger;

/**
 * This is hub for players that handles non-ingame client activity.
 * 
 * <li> registers connected players
 * <li> registers player game requests
 * <li> creates {@link GameRoom}s and adds players to them
 * TODO: clean finished games
 * TODO: better matchfinding logic
 * 
 * @author Fima
 *
 */
@Singleton
public class Hub implements IHub
{
	/**
	 * A logger
	 */
	private final Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * Queue of players looking for games
	 * TODO: this should probably be managed by separate game balancer class
	 */
//	private final Map <Character, GameType> queue = new IdentityHashMap<>();
	
	/**
	 * Currently open rooms that are waiting for players to join
	 */
	private final Multimap <String, GameRoom> pendingRoomsByType = HashMultimap.create();
	private final Map <Player, GameRoom> pendingRoomsByPlayer = new HashMap <> ();
	
	private final IGameFactory factory;
	
	/**
	 * Hub thread manages multiplayer game lifecycle
	 */
	private final IHubThread thread;
	
	private final IPlayerConnections connections;
	
	@Inject 
	public Hub(IGameFactory factory, IHubThread thread, IPlayerConnections connections)
	{
		this.factory = factory;
		this.thread = thread;
		this.connections = connections;

	}
	
	@Override
	public void init()
	{
		thread.init();
	}


	/**
	 * Registers player connection within the orchestrator.
	 * Called after player had successfully authenticated.
	 * @param handler
	 */
	@Override
	public boolean playerConnected( Player player, IPlayerHubDriver driver )
	{
		
		if(connections.hasPlayer( player ))
		{
			return false;
		}
		
		connections.putPlayer( player, driver );
		
		log.debug("Registered hub player %s.", player);
		// informing player driver that player is in hub now:
		driver.playerInHub();
		
		
		return true;
	}
	
	/**
	 * Unregisters player connection within the orchestrator.
	 * Called when player client disconnects from the server
	 * @param handler
	 */
	@Override
	public void playerDisconnected( Player player )
	{
		// removing from list of active players:
		connections.removePlayer( player );
			
		// removing from game rooms, if is in any:
		removeFromGameRoom( player );
		
		log.debug("Player %s left hub.", player);
	}

	/**
	 * Registers a game request.
	 * Note: This method runs in the client network IO thread! 
	 * TODO: separate game request, match finding and game launch processes
	 * @param player
	 * @param characterId
	 * @param gameType
	 */
	@Override
	public void addGameRequest(Player player, int characterId, String gameType) throws HubException
	{
		
		Character character = player.getCharacter( characterId );
		if(character == null)
		{
			log.error( "No character with id %d for player %s", characterId, player );
			return; // TODO: send error
		}
		
		IGamePlugin plugin = factory.getPlugin( gameType );
		if( plugin == null)
		{
			throw new HubException("Unknown game type [" + gameType + "].");
		}
		
		// updating pending characters queue:
//		queue.put( character, gameRequest.getGameType());
		
		// looking for suitable game
		// here should be
		synchronized(pendingRoomsByType)
		{
			Collection <GameRoom> gameCandidates = pendingRoomsByType.get( plugin.getName() );
			GameRoom gameroom = null;
			for(GameRoom aGame : gameCandidates)
			{
				if( !aGame.isFull() )
				{
					gameroom = aGame;
					break;
				}
			}
			
			if(gameroom == null)
			{			
	
				gameroom = new GameRoom( plugin, 2 );
				
				pendingRoomsByType.put( plugin.getName(), gameroom );
				pendingRoomsByPlayer.put( player, gameroom );
			}
		
			// adding character to the game:
			gameroom.addParticipant( player );
			

	
			if(gameroom.isFull())
			{
				pendingRoomsByType.remove( gameroom.getType(), gameroom );
				for(Player participant : gameroom.getParticipants())
				{
					pendingRoomsByPlayer.remove( participant );
				}
				
				// creating multiplayer game handler:
				thread.startGame( gameroom );
				
			}
		}
	}
	
	// TODO sync this with room start?
	@Override
	public void removeFromGameRoom( Player player )
	{
		synchronized(pendingRoomsByType)
		{			
			GameRoom room = pendingRoomsByPlayer.get( player );
			if(room == null)
			 {
				return; // not in room or ingame
			}
			room.removeParticipant( player );

			if(room.getParticipants().isEmpty())
			{
				// TODO: instead collect empty rooms after delay
				// TODO: explicit command to delete room
				pendingRoomsByType.remove( room.getType(), room );
			}
			
		}	
	}

	@Override
	public void safeStop()
	{
		log.debug( "Shutting down multiplayer hub thread..." );
		
		connections.safeStop();
		thread.safeStop();

		log.debug( "Multiplayer hub thread was shut down." );
	}


	@Override
	public void gameOver( Arena arena, IGameResults results )
	{
		// TODO Auto-generated method stub
		
	}


}
