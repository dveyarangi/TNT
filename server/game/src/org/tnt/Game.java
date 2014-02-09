/**
 * 
 */
package org.tnt;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Representation of game, managed by game server.
 * 
 * Keeps game state and configuration.
 */
public class Game implements Runnable
{
	/**
	 * Characters participating in this game
	 */
	private Set <Character> characters;
	
	/**
	 * Current ingame time (starts at 0)
	 */
	private long time;
	
	private boolean isAlive;
	
	private static ExecutorService threadPool = Executors.newCachedThreadPool();
	private static final long HEARTBEAT = 30; // ms
	
	public Game()
	{
		this.characters = new HashSet <Character> ();
		
		this.time = 0;
	}
	
	public void start()
	{
		 Future future = threadPool.submit( this );
	}

	@Override
	public void run()
	{
		while(isAlive)
		{
			// TODO: todo
			
			
			try
			{
				Thread.sleep( HEARTBEAT );
			} 
			catch( InterruptedException e ) { isAlive = false; }
		}
	}
}
