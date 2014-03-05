package org.tnt.game;

import gnu.trove.map.hash.TIntObjectHashMap;

import org.tnt.multiplayer.ICharacterAction;
import org.tnt.multiplayer.IGameResults;
import org.tnt.multiplayer.IGameUpdate;

/**
 * Interface for server-side game simulation process.
 * 
 * Game simulation receives player actions via {@link #addCharacterAction(int, ICharacterAction)} method,
 * and emit updates for game clients via {@link #getCharacterUpdate(int)}
 * 
 * 
 * @author Fima
 *
 */
public interface IGameSimulator
{
	/**
	 * Maximum characters number for this game
	 * @return
	 */
	public int getMaxCapacity();
	
	/**
	 * Initializes the simulator
	 */
	public void init();
	
	/**
	 * Steps game one tick forward; 
	 * @param time time of this frame
	 * @return false is the game is over
	 */
	public void step(long time, TIntObjectHashMap <IGameUpdate> updates);
	
	/**
	 * Destroys the simulator
	 */
	public void destroy();

	/**
	 * Inform simulator of character action
	 * @param pid
	 * @param action
	 */
	public void addCharacterAction( int pid, ICharacterAction action );

	/**
	 * Retrieve update for specified character
	 * @param pid
	 * @return
	 */
	public IGameUpdate getCharacterUpdate( int pid );
	
	/**
	 * Returns results if the simulation is over
	 * @return null if the simulation is in progress, results otherwise
	 */
	public IGameResults isOver();

}
