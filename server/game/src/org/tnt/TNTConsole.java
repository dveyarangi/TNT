package org.tnt;


public class TNTConsole implements Runnable
{
	
	private TNTServer server;
	private boolean isAlive = false;
	
	public TNTConsole(TNTServer server)
	{
		this.server = server;
	}
	
	
	private void clearConsole()
	{
	    try
	    {
	        String os = System.getProperty("os.name");

	        if (os.contains("Windows"))
	        {
	            Runtime.getRuntime().exec("cls");
	        }
	        else
	        {
	            Runtime.getRuntime().exec("clear");
	        }
	    }
	    catch (Exception exception)
	    {
	        //  Handle exception.
	    }
	}


	@Override
	public void run()
	{
		isAlive = true;
		while(isAlive)
		{
			clearConsole();
			
			
			System.out.println("============================================================================");
			System.out.println("= TNT game server (0.1.1)                                                α =");
			System.out.println("============================================================================");
			
			
			try
			{
				Thread.sleep( 1000 );
			}
			catch( InterruptedException e )
			{
				isAlive = false;
				break;
				
				// TODO: swallows this exception
			}
		}
		
		isAlive = false;
	}
}
