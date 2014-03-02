package com.character
{
	import com.events.CharacterEvent;
	import flash.display.MovieClip;
	import flash.events.Event;
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	
	/**
	 * ...
	 * @author Roman Rozanoff
	 */
	public class SimpleRat extends MovieClip implements ICharacter
	{
		
		public const TOP_SPEED:uint = 64;
		public const FAST_SPEED:uint = 52;
		public const MIN_SPEED:Number = 10; /// Character minimum speed (M/s)
		public const ACSELERATION:Number = 0.14; /// Character acselerattion (+M/s)
		
		private var speedTimer:Timer = new Timer(500);
		
		public function SimpleRat()
		{
		
		}
		
		public function setup():void
		{
			stage.addEventListener(CharacterEvent.CHANGE_STATE, onChangeStatus)
		}
		
		private function onChangeStatus(e:CharacterEvent):void
		{
			trace("RAT status: " + this.currentLabel);
			switch (MovieClip(root).charState)
			{
				case "wait": 
					gotoAndPlay("wait");
					stopSpeedTimer();
					break;
				
				case "acelerate": 
					gotoAndPlay("run");
					startSpeedTimer();
					break;
				
				case "break": 
					gotoAndPlay("break");
					stopSpeedTimer();
					break;
				
				case "run": 
					startSpeedTimer()
					checkSpeed();
					break;
				
				case "jump": 
					gotoAndPlay("jump");
					break;
			}
		}
		
		private function startSpeedTimer():void
		{
			speedTimer.addEventListener(TimerEvent.TIMER, checkSpeed);
			speedTimer.start();
		}
		
		private function stopSpeedTimer():void
		{
			speedTimer.removeEventListener(TimerEvent.TIMER, checkSpeed);
			speedTimer.stop();
		}
		
		private function checkSpeed(e:TimerEvent = null):void
		{
			if (MovieClip(root).engine.currentSpeed < FAST_SPEED)
			{
				if (this.currentLabel != "run")
				gotoAndPlay("run");
			}
			else
			{
				if (this.currentLabel != "fast")
				gotoAndPlay("fast");
			}
		}
	
	}

}