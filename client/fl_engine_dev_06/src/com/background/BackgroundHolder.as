package com.background 
{
	import com.background.elements.Arc;
	import com.events.GameEvent;
	import com.utils.RandomRange;
	import fl.motion.Color;
	import flash.display.MovieClip;
	import flash.display.Sprite;
	import flash.events.Event;
	
	/**
	 * ...
	 * @author Roman Rozanoff
	 */
	public class BackgroundHolder extends MovieClip 
	{
		
		private const ARCS_NUMBER:uint = 10; //tottal arcs number
		private const ARCS_STEP:uint = 1000; //arcs steep
		private const ARC_FRAMES:uint = 11; //different arcs number
		
		private var arcs:Vector.<Arc> = new Vector.<Arc>(); // holds all arcs
		
		private var col:Color = new Color();
		private var tempCol:Number=0;

		
		public function BackgroundHolder() 
		{
			addEventListener (Event.ADDED_TO_STAGE, init)
		}
		
		private function init(e:Event):void 
		{
			removeEventListener(Event.ADDED_TO_STAGE, init);
			setupArcs();
			stage.addEventListener(GameEvent.PLAYER_START_RUN, onPlayerStart);
			stage.addEventListener(GameEvent.END, onEndGame);
			
		}
		
		private function onEndGame(e:GameEvent):void 
		{
			removeEventListener(Event.ENTER_FRAME, updateView);
		}
		
		private function onPlayerStart(e:GameEvent):void 
		{
			addEventListener(Event.ENTER_FRAME, updateView);
		}
		
		private function setupArcs():void 
		{
			for (var i:uint = 0; i < ARCS_NUMBER; i++)
			{
				var arc:Arc = new Arc ();
				arc.x = 0;
				arc.y = 0;
				arc.z = (i * ARCS_STEP);
				addChild(arc);
				arcs[i] = arc;
				tempCol = - arc.z / 10000
				col.brightness = tempCol;
				arc.transform.colorTransform = col;
				arc.gotoAndStop (RandomRange.uintRandom(ARC_FRAMES, 1));
			}
		}
		
		private function updateView(e:Event ):void 
		{
			for (var i:uint = 0; i < ARCS_NUMBER; i++)
			{
				arcs[i].z -= Math.round( MovieClip(root).engine.currentSpeed * 2);
				
				if (arcs[i].z < 0)
				{
					arcs[i].z = ARCS_STEP * (ARCS_NUMBER + 1);
					arcs[i].gotoAndStop (RandomRange.uintRandom(ARC_FRAMES, 1));
				}
				
				tempCol = - arcs[i].z / 10000;
				
				if (tempCol < -0.8)
				{
					arcs[i].visible = false;
				} else {
					arcs[i].visible = true;
					col.brightness = tempCol;
					arcs[i].transform.colorTransform = col;
				}
				
			}
		}
		
	}

}