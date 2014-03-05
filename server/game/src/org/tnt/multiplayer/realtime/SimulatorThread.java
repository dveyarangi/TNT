package org.tnt.multiplayer.realtime;

import gnu.trove.map.hash.TIntObjectHashMap;

import org.tnt.game.IGameSimulator;
import org.tnt.multiplayer.IGameResults;
import org.tnt.multiplayer.IGameUpdate;
import org.tnt.multiplayer.MultiplayerGame;

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
	private long time;
	
	private final IGameSimulator simulator;
	
	private final MultiplayerGame multiplayer;
	
	////////////////////////////////////////////////////////////
	
	// thread service stuff
	private static Logger log = Logger.getLogger( SimulatorThread.class );
	
	private boolean isAlive;
	
	private boolean isPaused = true;
	
	private static final long HEARTBEAT = 30; // ms

	////////////////////////////////////////////////////////////
	
	public SimulatorThread(MultiplayerGame multiplayer, IGameSimulator simulator)
	{
		this.simulator = simulator;
		this.multiplayer = multiplayer;
	}
	
	@Override
	public void run()
	{
		
		TIntObjectHashMap <IGameUpdate> updatesBuffer = new TIntObjectHashMap <> ();
		
		long updateTime = startTime = System.nanoTime();
		long now;
		long stepTime;
		
		IGameResults results = null;
		
		simulator.init();
		
		isAlive = true;
		
		while( isAlive )
		{
			// testing game end condition:
			results = simulator.isOver();
			if(results != null)
			{
				break;
			}
			
			// registering step start time:
			now = System.nanoTime();
			
			if(!isPaused)
			{
				
				stepTime = now - updateTime;
				time += stepTime;

				// advancing game and getting updates
				simulator.step( stepTime, updatesBuffer );
				
				// dispatching updates
				// TODO: should be a separate controllable frequency
				// TODO: this copying is unnecessary
				for(int pid : updatesBuffer.keys())
				{
					multiplayer.addUpdate( pid, updatesBuffer.get( pid ) );
				}
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
