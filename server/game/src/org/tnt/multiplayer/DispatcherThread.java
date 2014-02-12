package org.tnt.multiplayer;

import io.netty.channel.Channel;

import java.util.Queue;

import org.tnt.IGameUpdate;
import org.tnt.account.Character;

import com.spinn3r.log5j.Logger;

public class DispatcherThread implements Runnable
{
	
	////////////////////////////////////////////////////////////
	//
	
	private MultiplayerGame multiplayer;
	
	////////////////////////////////////////////////////////////
	// thread service stuff
	
	
	private static Logger log = Logger.getLogger( DispatcherThread.class );
	
	private volatile boolean isAlive = false;
	
	private static final long HEARTBEAT = 50; // ms
	
	////////////////////////////////////////////////////////////
	
	
	public DispatcherThread(MultiplayerGame multiplayer)
	{
		this.multiplayer = multiplayer;
	}

	@Override
	public void run()
	{
		isAlive = true;
		
		Queue <IGameUpdate> updates;
		Channel channel;
		
		while(isAlive)
		{
			for(Character character : multiplayer.getCharacters())
			{
				updates = multiplayer.getUpdates( character );
				
				channel = multiplayer.getOrchestrator().getPlayerRegistery()
						.getChannel( character.getPlayer() );
				
				
				for(IGameUpdate update : updates)
				{
					channel.write( update ); 
				}
			}
			
			try
			{
				Thread.sleep(HEARTBEAT);
			}
			catch( InterruptedException e )
			{
				isAlive = false;
				log.fatal( e );
			}
		}
	}
	
	public void safeStop() { this.isAlive = false; }
	
	public String toString() { return "dispatcher-" + multiplayer.toString(); }
	
}