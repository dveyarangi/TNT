package com.events 
{
	import flash.events.Event;
	
	/**
	 * ...
	 * @author Roman Rozanoff
	 */
	public class UiEvent extends Event 
	{
		
		public static const UPDATE_UI:String = "updateUi"; // update UI
		
		public function UiEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false) 
		{ 
			super(type, bubbles, cancelable);
			
		} 
		
		public override function clone():Event 
		{ 
			return new UiEvent(type, bubbles, cancelable);
		} 
		
		public override function toString():String 
		{ 
			return formatToString("uiEvent", "type", "bubbles", "cancelable", "eventPhase"); 
		}
		
	}
	
}