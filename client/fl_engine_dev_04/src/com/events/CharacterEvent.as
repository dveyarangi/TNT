package com.events 
{
	import flash.events.Event;
	
	/**
	 * ...
	 * @author Roman Rozanoff
	 */
	public class CharacterEvent extends Event 
	{
		
		public static const CHANGE_STATE:String = "changeState"; // start game
		
		public function CharacterEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false) 
		{ 
			super(type, bubbles, cancelable);
			
		} 
		
		public override function clone():Event 
		{ 
			return new CharacterEvent(type, bubbles, cancelable);
		} 
		
		public override function toString():String 
		{ 
			return formatToString("CharacterEvent", "type", "bubbles", "cancelable", "eventPhase"); 
		}
		
	}
	
}