package org.tnt.realtime;

import org.tnt.game.Calculator;
import org.tnt.game.IGameResults;
import org.tnt.game.IGameSimulator;

import com.google.inject.Inject;
import com.spinn3r.log5j.Logger;

/**
 * This thread advances server-side game simulator
 * @author fimar
 *
 */
public class SimulatorThread implements Runnable
{
	////////////////////////////////////////////////////////////
	
	/**
	 * Current ingame time (starts at 0)
	 */
	private long startTime;
	private int time;
	
	private final IGameSimulator simulator;
	
	private final Arena multiplayer;
	
	////////////////////////////////////////////////////////////
	
	// thread service stuff
	private final Logger log = Logger.getLogger( this.getClass() );
	
	private boolean isAlive;
	
	private boolean isPaused = true;
	
	private static final long HEARTBEAT = 30; // ms
	
	@Inject private Calculator calculator; 
	
	////////////////////////////////////////////////////////////
	
	public SimulatorThread(Arena multiplayer, IGameSimulator simulator)
	{
		this.simulator = simulator;
		this.multiplayer = multiplayer;
	}
	
	@Override
	public void run()
	{
		
		long updateTime = startTime = calculator.getTime();
		long now;
		long stepTime;
		
		IGameResults results = null;
		
		isAlive = true;
		
		while( isAlive  )
		{
			// testing game end condition:
			results = simulator.isOver();
			if(results != null)
				break;
			
			// registering step start time:
			now = calculator.getTime();
			
			if(!isPaused)
			{
				
				stepTime = now - updateTime;
				time += stepTime;

				// advancing game and getting updates
				simulator.step( stepTime, time );

			}
			
			updateTime = now; 
			
			try
			{
				Thread.sleep( HEARTBEAT );
			} 
			catch( InterruptedException e ) { 
				isAlive = false; 
				log.error( "Simulator thread interrupted.", e );
			}
		}
		
		// disband the simulator:
		simulator.destroy();
		
		isAlive = false;
		
		// reporting results:
		if(results == null)
		{
			log.error( "Game failed to finish property." );
		} else
		{
			log.trace( "Game [" + multiplayer + "] finished in " + (startTime - System.currentTimeMillis()) + " ms.");
			multiplayer.gameOver( results );
		}
	}
	
	public void togglePause() 
	{ 
		this.isPaused = !this.isPaused;
		if(isPaused)
		{
			log.debug( "Game simulator is paused." );
		} else
		{
			log.debug( "Game simulator is unpaused." );
		}
	}
	
	public boolean isPaused() { return isPaused; }

	public void safeStop() { this.isAlive = false; }
	
	@Override
	public String toString() { return "simulator-" + multiplayer.toString(); }

	public IGameSimulator getSimulator() { return simulator; }
}
