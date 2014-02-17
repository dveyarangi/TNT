package org.tnt.multiplayer;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.List;

import org.tnt.IGameSimulator;
import org.tnt.IGameUpdate;

import com.spinn3r.log5j.Logger;

public class SimulatorThread implements Runnable
{
	////////////////////////////////////////////////////////////
	
	/**
	 * Current ingame time (starts at 0)
	 */
	private long startTime;
	private long time;
	
	private IGameSimulator simulator;
	
	private MultiplayerGame multiplayer;
	
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
		simulator.init();
		
		
		TIntObjectHashMap <IGameUpdate> updatesBuffer = new TIntObjectHashMap <> ();
		
		long updateTime = startTime = System.nanoTime();
		long now;
		
		while(isAlive && !simulator.isOver())
		{
			now = System.nanoTime();
			
			if(!isPaused)
			{
				
				time += (now - updateTime);

				// advancing game and getting updates
				simulator.step( time, updatesBuffer );
				
				// dispatching updates
				// TODO: should be a separate controllable frequency
				// TODO: this copying is unnecessary
				for(int pid : updatesBuffer.keys())
					multiplayer.addUpdate( pid, updatesBuffer.get( pid ) );
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
		
		multiplayer.gameOver();
	}
	
	public void togglePause() 
	{ 
		this.isPaused = !this.isPaused;
		if(isPaused)
			log.debug( "Game simulator is paused." );
		else
			log.debug( "Game simulator is unpaused." );
	}
	
	public boolean isPaused() { return isPaused; }

	public void safeStop() { this.isAlive = false; }
	
	public String toString() { return "simulator-" + multiplayer.toString(); }

	public IGameSimulator getSimulator() { return simulator; }
}
