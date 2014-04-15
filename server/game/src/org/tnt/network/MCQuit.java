package org.tnt.network;

import org.tnt.account.Player;
import org.tnt.halls.hub.IClientMessage;

/**
 * Game quit message from client.
 *
 * Should be send by client if it decides to leave a not started game room.
 * Ignored if sent after the game is started.
 * 
 * TODO: actually read this
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
	protected void process( final Player player )
	{
		// TODO: inform halls about player quit
		// hub.removeFromGameRoom( player );
	}

}
