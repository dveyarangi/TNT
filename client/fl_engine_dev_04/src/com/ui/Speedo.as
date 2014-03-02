package com.ui 
{
	import flash.display.MovieClip;
	import flash.events.Event;
	import flash.text.TextField;
	
	/**
	 * ...
	 * @author Roman Rozanoff
	 */
	public class Speedo extends MovieClip implements Iui
	{
		
		public var speed_txt:TextField;
		public var acceleration_txt:TextField;
		public var time_txt:TextField;
		
		private var displaySpeed:uint = 0;
		private var displayAcc:Number = 0;
		
		
		public function Speedo() 
		{
			
		}
		
		public function setup():void
		{
			reset();
		}
	
		public function reset():void
		{
			displaySpeed = 0;
			displayAcc = 0;
			speed_txt.text = displaySpeed.toString();
			acceleration_txt.text = displayAcc.toString();
			time_txt.text = "0:00";
		}
		
		public function update():void
		{
			displayAcc = MovieClip(root).engine.currentSpeed - displaySpeed; 
			displaySpeed = Math.round( MovieClip(root).engine.currentSpeed);
			speed_txt.text = displaySpeed.toString();
			acceleration_txt.text = displayAcc.toFixed(2);
			var seconds:String = Math.floor(MovieClip(root).engine.currentTime).toString();
			var milseconds:String = MovieClip(root).engine.currentTime.toFixed(2).toString();

			milseconds = milseconds.substring(milseconds.length-2,milseconds.length)

			time_txt.text = seconds + ":" + milseconds;	
		}

	}

}