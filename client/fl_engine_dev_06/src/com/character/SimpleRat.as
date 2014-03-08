package com.character
{
	import com.events.CharacterEvent;
	import com.events.GameEvent;
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
		
		public var actions:Array = new Array ( { type:"jump", name:"Jump", power:1, cntrlKey:32, time:1100 }, 
											   { type:"kick", name:"Kick", power:1, cntrlKey:49, time:750 }
											  );
		
		private var speedTimer:Timer = new Timer(500);
		private var actionTimer:Timer;
		
		public function SimpleRat()
		{
		
		}
		
		public function setup():void
		{
			stage.addEventListener(CharacterEvent.CHANGE_STATE, onChangeStatus);
			stage.addEventListener(CharacterEvent.ACTION, onAction);
		}
		
		private function onAction(e:CharacterEvent):void 
		{
			if (MovieClip(root).charState != "wait" && MovieClip(root).charState != "break" && MovieClip(root).charAction != null)
			{
				trace ("Caracter perfoming action: " + MovieClip(root).charAction.type)
				gotoAndPlay(MovieClip(root).charAction.type);
				stopSpeedTimer();
				setActionTimer(MovieClip(root).charAction.time);
				stage.dispatchEvent(new GameEvent (GameEvent.PLAYER_ACTION_START));
			}
		
		}
		
		private function setActionTimer(time:uint):void 
		{
			actionTimer = new Timer (time, 1);
			actionTimer.addEventListener (TimerEvent.TIMER_COMPLETE, endAction);
			actionTimer.start();
		}
		
		private function endAction(e:TimerEvent):void 
		{
			stage.dispatchEvent(new GameEvent (GameEvent.PLAYER_ACTION_END));
			actionTimer.removeEventListener (TimerEvent.TIMER_COMPLETE, endAction);
			actionTimer.stop();
			MovieClip(root).charAction = null;
			checkSpeed();
			
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
				{
					gotoAndPlay("fast");
					stopSpeedTimer();
				}
			}
		}
	
	}

}