/**
 * 
 */
package org.tnt.multiplayer;

import java.util.LinkedList;
import java.util.Queue;

import org.tnt.multiplayer.admin.IServerMessage;

import com.spinn3r.log5j.Logger;

/**
 * @author fimar
 *
 */
public class AdminDispatcherThread implements Runnable
{
	private volatile boolean isAlive = false;
	
	private MultiplayerHub orchestrator;
	
	private volatile Queue <IServerMessage> messageQueue = new LinkedList <IServerMessage> ();
	
	private static final long HEARTBEAT = 1000;
	
	private Logger log = Logger.getLogger( this.getClass() );
	
	public AdminDispatcherThread(MultiplayerHub orchestrator)
	{
		this.orchestrator = orchestrator;
	}
	
	public void addMessage(IServerMessage message)
	{
		synchronized (messageQueue)
		{
			messageQueue.add( message );
		}
	}
	
	
	@Override
	public void run()
	{
		isAlive = true;
		
		Queue <IServerMessage> queue;
		while(isAlive)
		{
			// sleeping some:
			try
			{
				Thread.sleep( HEARTBEAT );
			} catch( InterruptedException e )
			{
				log.error( "Dispatch loop interrupted", e );
				isAlive = false;
				break;
			}
			
			// swapping message queue:
			synchronized (messageQueue)
			{
				if(!messageQueue.isEmpty())
				{
					queue = messageQueue;
					messageQueue = new LinkedList <IServerMessage> ();
				} else
				{
					return;
				}
			}
			
			// processing message queue:
			while(!queue.isEmpty() && isAlive)
			{
				IServerMessage message = queue.poll();
				
			}
			
		}
		
		isAlive = false;
	}
	
	
	
	public void safeStop() { this.isAlive = false; }
}
