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
		public static const FALSE_START:String = "falseStart" //Players FALSE START
		public static const PLAYER_START_RUN:String = "playerStartRun"; //Player starts running
		public static const PLAYER_ACTION_START:String = "playerActionStart"; //Player starts running
		public static const PLAYER_ACTION_END:String = "playerActionEnd"; //Player starts running
		public static const PLAYER_HIT_OBS:String = "playerHitObsrtacle"; //Player hit pit or other obsrtacle
		public static const PLAYER_FINISHED:String = "playerFinished"; //Player end running
		public static const END:String = "end" //END game

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