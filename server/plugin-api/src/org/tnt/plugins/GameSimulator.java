package org.tnt.plugins;

import org.tnt.ICalculator;
import org.tnt.multiplayer.IArena;
import org.tnt.multiplayer.IAvatar;
import org.tnt.multiplayer.IAvatarUpdate;

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
public abstract class GameSimulator implements IGameSimulator
{
	
	private final IArena game;
	
	protected final Logger log = Logger.getLogger(this.getClass());
	
	private ICalculator calculator;
	
	public GameSimulator (IArena game)
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
	@Override
	public abstract void step(long stepTime, int gameTime);
	
	/**
	 * Destroys the simulator
	 */
	@Override
	public abstract void destroy();


	@Override
	public abstract IAvatarUpdate getStartingUpdate( int pid );
	
	protected IAvatar [] getAvatars() { return game.getAvatars(); }
 	
	/**
	 * Returns results if the simulation is over
	 * @return null if the simulation is in progress, results otherwise
	 */
	@Override
	public abstract IGameResults isOver();


}
