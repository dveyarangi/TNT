package org.tnt.multiplayer.realtime;

import java.util.Map;
import java.util.Queue;

import org.tnt.account.Character;
import org.tnt.multiplayer.ICharacterDriver;
import org.tnt.multiplayer.IGameUpdate;
import org.tnt.multiplayer.MultiplayerGame;

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
	
	private final Map <Character, ICharacterDriver> drivers;
	
	private final MultiplayerGame multiplayer;
	
	////////////////////////////////////////////////////////////
	
	
	public IngameDispatcherThread(MultiplayerGame multiplayer, Map <Character, ICharacterDriver> drivers)
	{
		this.multiplayer = multiplayer;
		this.drivers = drivers;
	}

	@Override
	public void run()
	{
		isAlive = true;
		int pid;
		
		Queue <IGameUpdate> updates;
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
			
			pid = 0;
			for(Character character : multiplayer.getCharacters())
			{
				updates = multiplayer.getUpdates( pid );
				
				
				for(IGameUpdate update : updates)
				{
					ICharacterDriver driver = drivers.get( character.getPlayer() );
					driver.update( update ); 
				}
				
				pid ++;
			}
			
		}
	}
	
	public void safeStop() { this.isAlive = false; }
	
	@Override
	public String toString() { return "dispatcher-" + multiplayer.toString(); }
	
	public void togglePause() { this.isPaused = !this.isPaused; }

}