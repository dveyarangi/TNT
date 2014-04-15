package org.tnt.halls.hub;

import org.tnt.account.Player;
import org.tnt.game.IGameResults;
import org.tnt.halls.realtime.Avatar;
import org.tnt.multiplayer.IAvatarDriver;
import org.tnt.multiplayer.network.hub.MSClose;






/**
 * Interface for talking to game lobby or room participant
 * 
 * @author Fima
 */
public interface IPlayerHubDriver
{
	/**
	 * Invoked when player enters the game room or game room changes.
	 * 
	 * @param room
	 */
	public void roomUpdated( HubRoom room );

	/**
	 * Invoked when the game starts
	 * @param game 
	 * @param pid
	 * @return Handle to control behavior of in-game character this player controls.
	 */
	public IAvatarDriver playerInGame( GameRoom room, Avatar avatar );
	/**
	 * Invoked when game ends.
	 * @param results
	 */
	void gameEnded( IGameResults results );

	void playerInHub();

	public void stop(MSClose reason);

	public Player getPlayer();
}
