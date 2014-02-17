package org.tnt.test;

import org.tnt.multiplayer.auth.MCAuth;

import com.google.gson.Gson;
import com.spinn3r.log5j.Logger;


public class MockGameClient extends Thread
{

	public static void main( String... args )
	{
		int playerId = Integer.valueOf( args[0] );
		int charId = Integer.valueOf( args[1] );
		new MockGameClient(playerId, charId).start();
	}
	
	
	private Logger log = Logger.getLogger(this.getClass());
	private Client client;
	private int	playerId;

	private int	charId;

	public MockGameClient(int playerId, int charId)
	{
		this.playerId = playerId;
		this.charId = charId;
		client = new Client(4242, playerId, charId );
	}
	
	@Override
	public void run()
	{
		Gson gson = new Gson();
		
		String authMessage = gson.toJson( new MCAuth( playerId ) );
//		ByteBuf buf = Unpooled.wrappedBuffer( authMessage.getBytes() ); 
		log.debug( "Sending message to server: " + authMessage );
		
		// sending auth message
		client.getChannel().writeAndFlush( authMessage + "\r\n" );
		
		while(true)
		{
			try
			{
				Thread.sleep( 1000 );
			}
			catch( InterruptedException e )
			{
				e.printStackTrace();
			}
		}
	}
}
