package org.tnt.multiplayer;

import java.util.Map;
import java.util.Queue;

import org.tnt.account.Character;
import org.tnt.multiplayer.realtime.IngameProtocolHandler;

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
	
	private Map <Character, IngameProtocolHandler> handlers;
	
	private MultiplayerGame multiplayer;
	
	////////////////////////////////////////////////////////////
	
	
	public IngameDispatcherThread(MultiplayerGame multiplayer, Map <Character, IngameProtocolHandler> handlers)
	{
		this.multiplayer = multiplayer;
		this.handlers = handlers;
	}

	@Override
	public void run()
	{
		isAlive = true;
		
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
				continue;
			
			for(Character character : multiplayer.getCharacters().keySet())
			{
				updates = multiplayer.getUpdates( multiplayer.getCharacters().get( character ) );
				
				
				for(IGameUpdate update : updates)
				{
					IngameProtocolHandler handler = handlers.get( character.getPlayer() );
					handler.write( update ); 
				}
			}
			
		}
	}
	
	public void safeStop() { this.isAlive = false; }
	
	public String toString() { return "dispatcher-" + multiplayer.toString(); }
	
	public void togglePause() { this.isPaused = !this.isPaused; }

}