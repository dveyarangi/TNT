package org.tnt.halls.realtime;

import com.spinn3r.log5j.Logger;

public class IngameDispatcherThread implements Runnable
{
	
	////////////////////////////////////////////////////////////
	//
	
	////////////////////////////////////////////////////////////
	// thread service stuff
	
	
	private static Logger log = Logger.getLogger( IngameDispatcherThread.class );
	
	private volatile boolean isAlive = false;
	
	private volatile boolean isPaused = true;
	
	private static final long HEARTBEAT = 50; // ms
	
	private final Avatar [] avatars;
	
	////////////////////////////////////////////////////////////
	
	
	public IngameDispatcherThread(Avatar [] avatars)
	{
		this.avatars = avatars;
	}

	@Override
	public void run()
	{
		isAlive = true;
		while(isAlive)
		{
			
			try
			{
				Thread.sleep(HEARTBEAT);
			}
			catch( InterruptedException e )
			{
				isAlive = false;
				log.fatal( e );
			}
			
			if(isPaused)
			{
				continue;
			}
			
			flushUpdates();
			
			
		}
	}
	
	private void flushUpdates()
	{

		for(int pid = 0; pid < avatars.length; pid ++)
		{
			avatars[pid].flushUpdates();
		}
	}
	
	public void safeStop() { this.isAlive = false; }
	
	public void togglePause() { 
		this.isPaused = !this.isPaused; 
		if(!isPaused)
		{
			flushUpdates();
		}
	}

}