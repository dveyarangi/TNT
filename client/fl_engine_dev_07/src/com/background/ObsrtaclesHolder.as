package com.background
{
	import com.background.elements.Pit;
	import com.events.GameEvent;
	import com.utils.RandomRange;
	import fl.motion.Color;
	import flash.display.MovieClip;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	
	/**
	 * ...
	 * @author Roman Rozanoff
	 */
	public class ObsrtaclesHolder extends MovieClip
	{
		
		private var frequency:Object = {low: 1500, hight: 4000};
		
		private var obsTimer:Timer = new Timer(2000, 1);
		
		private var col:Color = new Color();
		private var tempCol:Number = 0;
		
		private var obs:Array = new Array();
		
		public function ObsrtaclesHolder()
		{
			
			addEventListener(Event.ADDED_TO_STAGE, init)
		}
		
		private function init(e:Event):void
		{
			removeEventListener(Event.ADDED_TO_STAGE, init);
			stage.addEventListener(GameEvent.PLAYER_START_RUN, onPlayerStart);
			stage.addEventListener(GameEvent.END, onEndGame);
		}
		
		private function onPlayerStart(e:GameEvent):void
		{
			addEventListener(Event.ENTER_FRAME, moveObstracles);
			obsTimer.addEventListener(TimerEvent.TIMER_COMPLETE, generateObsrtacle);
			refreshObsTimer();
		
		}
		
		private function generateObsrtacle(e:TimerEvent):void
		{
			//trace ("NEW PIT! " + obsTimer.delay);
			var obstr = new Pit();
			obstr.z = 10000;
			obstr.x = RandomRange.intRandom(500);
			col.brightness = -1;
			obstr.transform.colorTransform = col;
			addChild(obstr);
			obs[this.numChildren - 1] = obstr;
			refreshObsTimer();
		}
		
		private function refreshObsTimer():void
		{
			obsTimer.reset();
			obsTimer.delay = RandomRange.uintRandom(frequency.hight, frequency.low);
			obsTimer.repeatCount = 1;
			obsTimer.start();
		}
		
		private function moveObstracles(e:Event):void
		{
			for (var i:uint = 0; i < this.numChildren; i++)
			{
				
				obs[i].z -= Math.round(MovieClip(root).engine.currentSpeed * 2);
				tempCol = -obs[i].z / 10000;
				if (tempCol < -0.9)
				{
					obs[i].visible = false;
				}
				else
				{
					obs[i].visible = true;
					col.brightness = tempCol;
					obs[i].transform.colorTransform = col;
				}
				
				if (obs[i].hitTestObject(MovieClip(root).rat.hit) && !MovieClip(root).god)
				{
					//trace ("EBAAA!");
					stage.dispatchEvent (new GameEvent(GameEvent.PLAYER_HIT_OBS));
				}
				
				if (obs[i] <= 40)
				{
					obs[i].splice(i, 1);
					removeChildAt(i);
				}

				
				

			}
		}
		
		private function onEndGame(e:GameEvent):void
		{
			removeEventListener(Event.ENTER_FRAME, moveObstracles);
			obsTimer.stop();
			obsTimer.removeEventListener(TimerEvent.TIMER_COMPLETE, generateObsrtacle);
		}
	
	}

}