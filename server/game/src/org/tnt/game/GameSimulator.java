package org.tnt.game;

import org.tnt.ICalculator;
import org.tnt.multiplayer.IGameResults;
import org.tnt.multiplayer.realtime.Arena;
import org.tnt.multiplayer.realtime.Avatar;
import org.tnt.multiplayer.realtime.IAvatarUpdate;

import com.google.inject.Inject;
import com.spinn3r.log5j.Logger;

/**
 * Interface for server-side game simulation process.
 * 
 * Implementation may access character channel via {@link #getAvatars()}
 * and put character state updates via {@link #putCharacterUpdate(int, IAvatarUpdate)}  
 * 
 * @author Fima
 *
 */
public abstract class GameSimulator
{
	
	private final Arena game;
	
	protected final Logger log = Logger.getLogger(this.getClass());;
	
	@Inject private ICalculator calculator;
	
	public GameSimulator (Arena game)
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
	public abstract void step(long stepTime, int gameTime);
	
	/**
	 * Destroys the simulator
	 */
	public abstract void destroy();


	public abstract IAvatarUpdate getStartingUpdate( int pid );
	
	protected Avatar [] getAvatars() { return game.getAvatars(); }
 	
	/**
	 * Returns results if the simulation is over
	 * @return null if the simulation is in progress, results otherwise
	 */
	public abstract IGameResults isOver();


}
