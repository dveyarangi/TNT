package org.tnt.multiplayer;

import org.tnt.multiplayer.network.hub.MSClose;
import org.tnt.multiplayer.realtime.Avatar;
import org.tnt.multiplayer.realtime.ICharacterDriver;






/**
 * Interface for talking to game lobby or room participant
 * 
 * @author Fima
 */
public interface IPlayerDriver
{
	/**
	 * Invoked when player enters the game room or game room changes.
	 * 
	 * @param room
	 */
	void gameRoomUpdated( GameRoom room );

	/**
	 * Invoked when the game starts
	 * @param game 
	 * @param pid
	 * @return Handle to control behavior of in-game character this player controls.
	 */
	public ICharacterDriver playerInGame( GameRoom room, Avatar avatar );
	/**
	 * Invoked when game ends.
	 * @param results
	 */
	void gameEnded( IGameResults results );

	void playerInHub( IHub hub );

	public void stop(MSClose reason);
}
