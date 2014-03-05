package org.tnt.multiplayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.tnt.account.Character;
import org.tnt.account.Player;
import org.tnt.game.IGamePlugin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.spinn3r.log5j.Logger;

/**
 * Matches players to games
 * TODO: clean finished games
 * TODO: better matchfinding logic
 * 
 * @author Fima
 *
 */
public class Hub
{
	/**
	 * A logger
	 */
	private final Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * List of players currently connected to players.
	 * Each player has a corresponding channel handler, that manages current communication protocol with player's client
	 */
	private final Map <Player, IPlayerDriver> activePlayers = new HashMap <> ();
	
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
	
	/**
	 * Hub thread manages multiplayer game lifecycle
	 */
	private final HubThread thread;
	
	public Hub()
	{
		thread = new HubThread( this );
		
		thread.start();
	}

	
	/**
	 * Registers player connection within the orchestrator.
	 * Called after player had successfully authenticated.
	 * @param handler
	 */
	public boolean registerPlayerHandler( Player player, PlayerHubDriver handler )
	{
		if(activePlayers.containsKey( player ))
		{
			return false;
		}
		log.debug("Registered player handler for player " + player);
		activePlayers.put( player, handler );
		
		return true;
	}
	
	/**
	 * Unregisters player connection within the orchestrator.
	 * Called when player client disconnects from the server
	 * @param handler
	 */
	public void unregisterPlayerHandler( Player player )
	{
		log.debug("Unregistered player handler for player " + player);
		activePlayers.remove( player );
			
		removeFromGame( player );
	}

	/**
	 * Registers a game request.
	 * Note: This method runs in the client network IO thread! 
	 * TODO: separate game request, match finding and game launch processes
	 * @param player
	 * @param characterId
	 * @param gamePlugin
	 */
	public void addGameRequest(Player player, int characterId, IGamePlugin gamePlugin)
	{
		
		Character character = player.getCharacter( characterId );
		if(character == null)
		{
			log.error( "No character with id %d for player %s", characterId, player );
			return; // TODO: send error
		}
		
		// updating pending characters queue:
//		queue.put( character, gameRequest.getGameType());
		
		// looking for suitable game
		// here should be
		synchronized(pendingRoomsByType)
		{
			Collection <GameRoom> gameCandidates = pendingRoomsByType.get( gamePlugin.getName() );
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
	
				gameroom = new GameRoom( gamePlugin, 2 );
				
				pendingRoomsByType.put( gamePlugin.getName(), gameroom );
				pendingRoomsByPlayer.put( player, gameroom );
			}
		
			// adding character to the game:
			gameroom.addCharacter( character );
			
			// sending game details message:
			for(Character roomChar : gameroom.getCharacters())
			{
				IPlayerDriver driver = activePlayers.get( roomChar.getPlayer() );
				
				driver.gameRoomUpdated( gameroom );
			}
	
			if(gameroom.isFull())
			{
				pendingRoomsByType.remove( gameroom.getType(), gameroom );
				for(Character roomChar : gameroom.getCharacters())
				{
					pendingRoomsByPlayer.remove( roomChar.getPlayer() );
				}
				
				// creating multiplayer game handler:
				MultiplayerGame game = new MultiplayerGame( gameroom );
				thread.startGame(game);
				
			}
		}
	}
	
	// TODO sync this with room start
	public void removeFromGame( Player player )
	{
		synchronized(pendingRoomsByType)
		{			
			GameRoom room = pendingRoomsByPlayer.get( player );
			if(room == null)
			 {
				return; // not in room or ingame
			}
			room.removeCharacter( player );

			if(room.getCharacters().isEmpty())
			{
				pendingRoomsByType.remove( room.getType(), room );
			}
			
		}	
	}


	IPlayerDriver getPlayer( Player player )
	{
		return activePlayers.get( player );
	}

}
