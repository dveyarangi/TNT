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
	
	public GameType getType();

	public int getMaxCapacity();
	
	public void init();
	
	/**
	 * Steps game one tick forward; 
	 * @param time
	 * @return false is the game is over
	 */
	public void step(long time, TIntObjectHashMap <IGameUpdate> updates);

	public void addCharacterAction( int pid, ICharacterAction action );

	public IGameUpdate getCharacterUpdate( int pid );
	
	public IGameResults isOver();

}
