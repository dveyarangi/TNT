package org.tnt.hub;

import org.tnt.account.Player;
import org.tnt.network.IClientMessage;

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


	public MCGameRequest ( final String type, final int charId )
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
	protected void process( final Player player, final IHub hub ) throws HubException
	{

		hub.addGameRequest( player, characterId, gameType );
	}


}
