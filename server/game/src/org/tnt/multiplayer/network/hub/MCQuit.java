package org.tnt.multiplayer.network.hub;

import org.tnt.account.Player;
import org.tnt.multiplayer.IHub;

/**
 * Game quit message from client.
 *
 * Should be send by client if it decides to leave a not started game room.
 * Ignored if sent after the game is started.
 * 
 * @author fimar
 */
public class MCQuit extends IClientMessage
{

	/**
	 * {@inheritDoc}
	 * 
	 * Removing the player from his game room (if he is in any)
	 */
	@Override
	void process( Player player, IHub hub )
	{
		hub.removeFromGameRoom( player );
	}

}
