package org.tnt.multiplayer;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.tnt.GameType;
import org.tnt.IGameSimulator;
import org.tnt.account.Character;
import org.tnt.account.Player;
import org.tnt.account.PlayerStore;
import org.tnt.game.SimulatorFactory;
import org.tnt.multiplayer.admin.MCGameRequest;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Matches players to games
 * TODO: clean finished games
 * TODO: better matchfinding logic
 * 
 * 
 * @author Fima
 *
 */
public class MultiplayerOrchestrator
{
	
	private ConnectedPlayersRegistery registery;
	
	public Map <Character, GameType> queue = new IdentityHashMap<Character, GameType>();
	
	public Multimap <GameType, MultiplayerGame> initingGames = HashMultimap.create();
	
	public Map <Player, MultiplayerGame> runningGames = new IdentityHashMap<Player, MultiplayerGame> ();
	
	private SimulatorFactory gameFactory = new SimulatorFactory();
	
	
	private ExecutorService threadPool = Executors.newCachedThreadPool();
	
	public MultiplayerOrchestrator(PlayerStore store)
	{
		registery = new ConnectedPlayersRegistery( store );
	}
	
	/**
	 * Registers a game request.
	 * Note: This method runs in the client network IO thread! 
	 * @param player
	 * @param characterId
	 * @param gameRequest
	 */
	public void addGameRequest(Player player, int characterId, MCGameRequest gameRequest)
	{
		
		Character character = player.getCharacters().get( characterId );
		
		GameType type = gameRequest.getGameType();
		
		// updating pending characters queue:
//		queue.put( character, gameRequest.getGameType());
		
		// looking for suitable game
		// here should be
		Collection <MultiplayerGame> gameCandidates = initingGames.get( type );
		MultiplayerGame game = null;
		for(MultiplayerGame aGame : gameCandidates)
		{
			if( !aGame.getSimulator().isFull() )
			{
				game = aGame;
				break;
			}
		}
		
		if(game == null)
		{
			IGameSimulator simulator = gameFactory.getSimulation( type );
			game = new MultiplayerGame( this, simulator );
			
			initingGames.put( type, game );
		}
	
		// adding character to the game:
		game.addCharacter( character );

		
		if(game.getSimulator().isFull())
		{
			synchronized(initingGames)
			{
				initingGames.remove( game.getSimulator().getType(), game );
			}
			
			synchronized(runningGames)
			{
				for(Character gameCharacter : game.getCharacters())
				{
					runningGames.put( gameCharacter.getPlayer(), game );
				}
			}
			
			game.ready();
		}
	}

	public ConnectedPlayersRegistery getPlayerRegistery() { return registery; }
	

	
	void gameReady( MultiplayerGame game )
	{
		for(Character gameCharacter : game.getCharacters())
		{
			registery.getPlayerHandler( gameCharacter.getPlayer() ).switchToRealTime(game, gameCharacter);
		}
		
		game.start( threadPool );
		
	}

	void gameOver( MultiplayerGame game )
	{
		synchronized(runningGames)
		{
			for(Character gameCharacter : game.getCharacters())
			{
				runningGames.remove( gameCharacter.getPlayer() );
				registery.getPlayerHandler( gameCharacter.getPlayer() ).switchToAdmin();
			}
		}
		
	}

	public MultiplayerGame getGame( Player player )
	{
		return runningGames.get( player );
	}


	
}
