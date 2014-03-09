package org.tnt.multiplayer.network.hub;

import org.tnt.account.Player;
import org.tnt.game.GameFactory;
import org.tnt.game.IGamePlugin;
import org.tnt.multiplayer.Hub;

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
	private final String gameType;
	
	/**
	 * Character selected to participate in the game
	 */
	private final int characterId;


	public MCGameRequest ( String type, int charId )
	{
		this.gameType = type;
		this.characterId = charId;
	}

	
	/**
	 * {@inheritDoc}
	 * 
	 * Registers this game request with the multiplayer orchestrator.
	 */
	@Override
	void process( Player player, Hub orchestrator ) throws HubException
	{
		IGamePlugin plugin = GameFactory.getPlugin( gameType );
		if( plugin == null)
			throw new HubException("Unknown game type [" + gameType + "].");
		
		orchestrator.addGameRequest( player, characterId, plugin );
	}
	
	
}
