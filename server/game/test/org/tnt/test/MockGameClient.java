package org.tnt.test;

import org.tnt.multiplayer.auth.MCAuth;

import com.google.gson.Gson;


public class MockGameClient extends Thread
{

	public static void main( String... args )
	{
		new MockGameClient().start();
	}
	
	private Client client;
	public MockGameClient()
	{
		client = new Client(4242);
	}
	
	@Override
	public void run()
	{
		Gson gson = new Gson();
		
		String authMessage = gson.toJson( new MCAuth(1) );
//		ByteBuf buf = Unpooled.wrappedBuffer( authMessage.getBytes() ); 
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
