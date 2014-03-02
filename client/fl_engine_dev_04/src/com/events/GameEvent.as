package com.events 
{
	import flash.events.Event;
	
	/**
	 * ...
	 * @author Roman Rozanoff
	 */
	public class GameEvent extends Event 
	{
		
		public static const START:String = "start"; // start game
		public static const START_RUN:String = "startRun"; //Player starts running
		public static const END:String = "end" //END game
		public static const FALSE_START:String = "falsStart" //Players FALSE START
		
		
		public function GameEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false) 
		{ 
			super(type, bubbles, cancelable);	
		} 
		
		public override function clone():Event 
		{ 
			return new GameEvent(type, bubbles, cancelable);
		} 
		
		public override function toString():String 
		{ 
			return formatToString("GameEvent", "type", "bubbles", "cancelable", "eventPhase"); 
		}
		
	}
	
}