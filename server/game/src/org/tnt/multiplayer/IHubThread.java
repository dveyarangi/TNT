package org.tnt.multiplayer;

import org.tnt.multiplayer.realtime.Arena;

public interface IHubThread extends Runnable
{

	/**
	 * Starts multiplayer game from the specified room
	 * @param gameroom
	 */
	public abstract void startGame( GameRoom room );

	public abstract void gameOver( Arena game, IGameResults results );

	public abstract void safeStop();

	public abstract void init();

}
