package org.tnt.multiplayer;




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
	public ICharacterDriver gameStarted(MultiplayerGame game, int pid);

	/**
	 * Invoked when game ends.
	 * @param results
	 */
	void gameEnded( IGameResults results );

}
