package com.ui.elements 
{
	import com.events.CharacterEvent;
	import flash.display.MovieClip;
	import flash.events.Event;
	import flash.events.KeyboardEvent;
	import flash.events.MouseEvent;
	import flash.text.TextField;
	
	/**
	 * ...
	 * @author Roman Rozanoff
	 */
	public class ActionBtn extends MovieClip 
	{
		public var describtion:Object;
		public var label:TextField;
		
		//{ type:"jump", name:"Jump", power:1, cntrlKey:32, time:1100 }
		
		public function ActionBtn(_desc:Object) 
		{
			describtion = _desc;
			addEventListener(Event.ADDED_TO_STAGE, init);
		}
		
		private function init(e:Event):void 
		{
			removeEventListener(Event.ADDED_TO_STAGE, init);
			label.text = describtion.name;
			buttonMode = true;
			mouseChildren = false;
			addEventListener(MouseEvent.CLICK, onActionClick);
			stage.addEventListener(KeyboardEvent.KEY_DOWN, actionKeyDown);
			
		}
		
		private function actionKeyDown(e:KeyboardEvent):void 
		{
			if (e.charCode == describtion.cntrlKey)
			{
				//trace (displayName + " key pressed");
				onActionClick(null);
			}
		}
		
		private function onActionClick(e:MouseEvent):void 
		{
			MovieClip(root).charAction = this.describtion;
			stage.dispatchEvent (new CharacterEvent(CharacterEvent.ACTION));
		}
		
	}

}