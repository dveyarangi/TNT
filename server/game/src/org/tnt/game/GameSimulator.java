package org.tnt.game;

import java.util.Queue;

import org.tnt.multiplayer.ICharacterAction;
import org.tnt.multiplayer.IGameResults;
import org.tnt.multiplayer.IGameUpdate;
import org.tnt.multiplayer.MultiplayerGame;

/**
 * Interface for server-side game simulation process.
 * 
 * Implementation may access character input by {@link #getCharacterAction(int)}
 * and put character state updates via {@link #putCharacterUpdate(int, IGameUpdate)}  
 * 
 * @author Fima
 *
 */
public abstract class GameSimulator
{
	
	private final MultiplayerGame game;
	
	public GameSimulator (MultiplayerGame game)
	{
		this.game = game;
	}
	
	/**
	 * Maximum characters number for this game
	 * @return
	 */
	public abstract int getMaxCapacity();
	
	/**
	 * Steps game one tick forward; 
	 * @param time time of this frame
	 * @param time2 
	 * @return false is the game is over
	 */
	public abstract void step(long stepTime, long gameTime);
	
	/**
	 * Destroys the simulator
	 */
	public abstract void destroy();


	public abstract IGameUpdate getStartingUpdate( int pid );
	
	/**
	 * Inform simulator of character action
	 * @param pid
	 * @param action
	 */
	public void putCharacterUpdate( int pid, IGameUpdate update )
	{
		game.putCharacterUpdate(pid, update);
	}

	/**
	 * Retrieve update for specified character
	 * @param pid
	 * @return
	 */
	protected Queue <ICharacterAction> getCharacterAction( int pid )
	{
		return game.getCharacterAction( pid );
	}
	
	/**
	 * Returns results if the simulation is over
	 * @return null if the simulation is in progress, results otherwise
	 */
	public abstract IGameResults isOver();


}
