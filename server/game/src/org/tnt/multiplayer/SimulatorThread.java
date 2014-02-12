package org.tnt.multiplayer;

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
		
		List <IGameUpdate> updates;
		
		startTime = System.nanoTime();
		while(isAlive && !simulator.isOver())
		{
			// getting ingame time
			time = System.nanoTime() - startTime;
			
			// advancing game and getting updates
			updates = simulator.step( time );
			
			// dispatching updates
			// TODO: should be a separate controllable frequency
			for(IGameUpdate update : updates)
				multiplayer.addUpdate( update );
					
			try
			{
				Thread.sleep( HEARTBEAT );
			} 
			catch( InterruptedException e ) { isAlive = false; }
		}
		
		multiplayer.gameOver();
	}

	public void safeStop() { this.isAlive = false; }
	
	public String toString() { return "simulator-" + multiplayer.toString(); }

	public IGameSimulator getSimulator() { return simulator; }
}
