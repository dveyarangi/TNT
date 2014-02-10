package org.tnt.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;


public class MockGameClient extends Thread
{

	public static void main( String... args )
	{
		new MockGameClient().start();
	}
	
	private TestClientHandler handler;
	private Client client;
	public MockGameClient()
	{
		handler = new TestClientHandler();
		client = new Client(4242, handler);


	}
	
	public void run()
	{
		ByteBufAllocator allocator = new UnpooledByteBufAllocator(false);
		ByteBuf buf = allocator.buffer(); 
		buf.setBytes( 0, new byte [] { 1, 2 ,3 ,4 } );
		while(true)
		{
			
			client.getChannel().write( buf );
			
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
