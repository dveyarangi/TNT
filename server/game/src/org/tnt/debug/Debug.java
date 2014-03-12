package org.tnt.debug;

import com.slytechs.debug.swing.StopButtonFrame;

public class Debug
{
	public static void init()
	{
		StopButtonFrame stop = new StopButtonFrame(20, 20) {   
		    @Override
			public void processStopButtonAction() {  
		        System.exit(0);  
		    };  
		};  		
		
		stop.setVisible( true );
	}
}
