package org.tnt.multiplayer.admin;

import org.tnt.account.Player;
import org.tnt.game.GameType;
import org.tnt.multiplayer.MultiplayerHub;

/**
 * Game request message from client.
 * 
 * @author fimar
 */
public class MCGameRequest extends IClientMessage
{
	/**
	 * Requested game type
	 */
	private GameType gameType;
	
	/**
	 * Character selected to participate in the game
	 */
	private int characterId;

	
	/**
	 * 
	 * @param gameType
	 * @param characterId
	 */
	public MCGameRequest( GameType gameType, int characterId )
	{
		super();
		this.gameType = gameType;
		this.characterId = characterId;
	}


	public GameType getGameType() {	return gameType; }


	/**
	 * {@inheritDoc}
	 * 
	 * Registers this game request with the multiplayer orchestrator.
	 */
	@Override
	void process( Player player, MultiplayerHub orchestrator )
	{
		orchestrator.addGameRequest( player, characterId, this );
	}
	
	
}
