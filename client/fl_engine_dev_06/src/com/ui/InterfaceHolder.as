package com.ui 
{
	import com.events.GameEvent;
	import com.events.UiEvent;
	import com.greensock.plugins.AutoAlphaPlugin;
	import com.greensock.plugins.TweenPlugin;
	import com.greensock.TweenLite;
	import flash.display.MovieClip;
	import flash.display.SimpleButton;
	import flash.events.Event;
	import flash.events.MouseEvent;
	
	/**
	 * ...
	 * @author Roman Rozanoff
	 */
	public class InterfaceHolder extends MovieClip 
	{
		
		public var startBtn:SimpleButton;
		public var charControlsHolder:CharControlsHolder;
		public var map:Map;
		public var speedo:Speedo;
		
		public function InterfaceHolder() 
		{
			addEventListener (Event.ADDED_TO_STAGE, init)
		}
		
		private function init(e:Event):void 
		{
			removeEventListener(Event.ADDED_TO_STAGE, init);
			TweenPlugin.activate([AutoAlphaPlugin]);
			stage.addEventListener (UiEvent.READY_FOR_UI, onDataReadyForUi);
		}
		
		private function onDataReadyForUi(e:UiEvent):void 
		{
			stage.removeEventListener (UiEvent.READY_FOR_UI, onDataReadyForUi);
			startBtn.addEventListener(MouseEvent.CLICK, startRun);
			map.setup();
			speedo.setup();
			charControlsHolder.setup();
			stage.addEventListener (UiEvent.UPDATE_UI, updateUi);
		}
		
		private function startRun(e:MouseEvent):void 
		{
			trace("UI: start btn pressed");
			stage.dispatchEvent(new GameEvent(GameEvent.PLAYER_START_RUN));
			TweenLite.to(e.currentTarget, 0.5, { scaleX:0.1, scaleY:0.1, autoAlpha:0 } );
			
			
		}
		
		private function updateUi(e:UiEvent):void 
		{
			speedo.update();
			map.update();
		}
		
	}

}