package org.tnt.multiplayer;

import io.netty.channel.Channel;

import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.tnt.account.Character;
import org.tnt.account.Player;
import org.tnt.account.PlayerStore;
import org.tnt.game.GameFactory;
import org.tnt.game.GameType;
import org.tnt.multiplayer.admin.MCGameRequest;
import org.tnt.multiplayer.admin.MSGameDetails;
import org.tnt.multiplayer.admin.MSGo;
import org.tnt.multiplayer.realtime.IngameProtocolHandler;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.spinn3r.log5j.Logger;

/**
 * Matches players to games
 * TODO: clean finished games
 * TODO: better matchfinding logic
 * TODO: abstractize gamehandlerprotocol to allow bots
 * 
 * @author Fima
 *
 */
public class MultiplayerHub
{
	/**
	 * A logger
	 */
	private Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * List of players currently connected to players.
	 * Each player has a corresponding channel handler, that manages current communication protocol with player's client
	 */
	private Map <Player, GameProtocolHandler> activePlayers = new HashMap <> ();
	
	/**
	 * Queue of players looking for games
	 * TODO: this should probably be managed by separate game balancer class
	 */
	private Map <Character, GameType> queue = new IdentityHashMap<>();
	
	/**
	 * Currently open rooms that are waiting for players to join
	 */
	private Multimap <GameType, GameRoom> pendingRoomsByType = HashMultimap.create();
	private Map <Player, GameRoom> pendingRoomsByPlayer = new HashMap <> ();
	
	private Queue <GameRoom> readyRooms = new LinkedList <GameRoom> ();
	
	/**
	 * Registry of games in progress
	 */
	private Map <Player, MultiplayerGame> runningGames = new IdentityHashMap<> ();
	
	
	private GameFactory gameFactory = new GameFactory();
	
	/**
	 * Player data storage
	 */
	private PlayerStore store;
	
	/**
	 * executor pool for game threads
	 */
	private ExecutorService threadPool = Executors.newCachedThreadPool();
	
	public MultiplayerHub(PlayerStore store)
	{
		this.store = store;
//		registery = new ConnectedPlayersRegistery( store );
	}

	/**
	 * Registers player connection within the orchestrator.
	 * Called after player had successfully authenticated.
	 * @param handler
	 */
	public boolean registerPlayerHandler( Player player, GameProtocolHandler handler )
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
	 * @param gameRequest
	 */
	public void addGameRequest(Player player, int characterId, MCGameRequest gameRequest)
	{
		
		Character character = player.getCharacter( characterId );
		if(character == null)
		{
			log.error( "No character with id %d for player %s", characterId, player );
			return; // TODO: send error
		}
		
		GameType type = gameRequest.getGameType();
		
		// updating pending characters queue:
//		queue.put( character, gameRequest.getGameType());
		
		// looking for suitable game
		// here should be
		synchronized(pendingRoomsByType)
		{
			Collection <GameRoom> gameCandidates = pendingRoomsByType.get( type );
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
	
				gameroom = new GameRoom( type, 2 );
				
				pendingRoomsByType.put( type, gameroom );
				pendingRoomsByPlayer.put( player, gameroom );
			}
		
			// adding character to the game:
			gameroom.addCharacter( character );
			
			// sending game details message:
			for(Character roomChar : gameroom.getCharacters())
			{
				MSGameDetails details = new MSGameDetails( gameroom.getCharacters() );
				
				GameProtocolHandler handler = activePlayers.get( roomChar.getPlayer() );
				
				handler.getAdminHandler().write( details );
			}
	
			if(gameroom.isFull())
			{
				pendingRoomsByType.remove( gameroom.getType(), gameroom );
				for(Character roomChar : gameroom.getCharacters())
				{
					pendingRoomsByPlayer.remove( roomChar.getPlayer() );
				}
				
				initGame(gameroom);
				
			}
		}
	}
	
	
	/**
	 * Starts multiplayer game from the specified room
	 * @param gameroom
	 */
	private void initGame( GameRoom gameroom )
	{
		// creating multiplayer game handler:
		MultiplayerGame game = new MultiplayerGame(this, gameFactory, gameroom);
		
		Map <Character, IngameProtocolHandler> handlers = new HashMap <> ();
		
		synchronized(runningGames)
		{
			for(Character gameCharacter : game.getCharacters().keySet())
			{
				Player player = gameCharacter.getPlayer();
				
				GameProtocolHandler handler = activePlayers.get( player );
				
				// sending game ready to all participants:
				handler.getAdminHandler().write( MSGo.GO );
				
				
				/////////////////////////////////////////////////////////////////////
				// this was the last admin message, now real-time protocol starts
				
				// swapping to real time protocol:
				handlers.put( gameCharacter, handler.switchToRealTime( game, game.getCharacters().get( gameCharacter ) ) );
				
				// updating running games registry:
				runningGames.put( player, game );
			}
		}	
		
		// starting game:
		game.start( threadPool, handlers );
	}
	
	
	void gameOver( MultiplayerGame game )
	{
		synchronized(runningGames)
		{
			for(Character gameCharacter : game.getCharacters().keySet())
			{
				runningGames.remove( gameCharacter.getPlayer() );
				
				activePlayers.get( gameCharacter.getPlayer() ).switchToAdmin();
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

	public IngameProtocolHandler createHandler( Channel channel, MultiplayerGame multiplayer, int pid )
	{
		return gameFactory.getIngameHandler( channel, multiplayer, pid );

	}
}
