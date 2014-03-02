package com 
{
	import com.background.BackgroundHolder;
	import com.character.SimpleRat;
	import com.events.GameEvent;
	import com.ui.InterfaceHolder;
	import flash.display.MovieClip;
	import flash.events.Event;
	
	/**
	 * ...
	 * @author Roman Rozanoff
	 */
	

	public class GameDoc extends MovieClip 
	{
		///////////////////////////////////////////////////////////////// hardcoded settings. WILL BE CHANGED BY DIMANIC
		public var distance:uint = 500; /// Distance to run (Meter)
		public var topSpeed:Number = 64; /// Character top speed (M/s)
		public var minSpeed:Number = 10; /// Character minimum speed (M/s)
		public var acseleration:Number = 0.014; /// Character acselerattion (+M/s)
		public var charState:String = "wait"; /// Character State 

		/////////////////////////////////////////////////////////////////

		public var backHolder:BackgroundHolder;
		public var interfaceHolder:InterfaceHolder;
		public var engine:GameEngine;
		public var rat:SimpleRat;
		
		public function GameDoc() 
		{
			addEventListener (Event.ADDED_TO_STAGE, init);
		}
		
		private function init(e:Event = null):void 
		{
			removeEventListener(Event.ADDED_TO_STAGE, init);
			
			setupChar();
			
			engine = new GameEngine();
			addChild(engine);
			engine.setupNewGame();
			
			stage.dispatchEvent(new GameEvent(GameEvent.START));
	
		}
		
		private function setupChar():void 
		{
			topSpeed = rat.TOP_SPEED;
			minSpeed = rat.MIN_SPEED;
			acseleration = rat.ACSELERATION;
			rat.setup();
		}
		
	}

}