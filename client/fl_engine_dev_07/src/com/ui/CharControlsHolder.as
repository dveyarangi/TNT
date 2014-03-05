package com.ui 
{
	import com.ui.elements.ActionBtn;
	import flash.display.MovieClip;
	
	/**
	 * ...
	 * @author Roman Rozanoff
	 */
	public class CharControlsHolder extends MovieClip implements Iui 
	{
		private var actionBtns:Array = new Array();
		private const Y_STEEP:uint = 41;
		
		public function CharControlsHolder() 
		{
			
		}
		
		/* INTERFACE com.ui.Iui */
		public function reset():void 
		{
			for (var i:uint = 0; i < actionBtns.length; i++)
			{
				removeChild(actionBtns[i]);
			}
		}
		
		public function update():void 
		{
			
		}
		
		public function setup():void 
		{
			for (var i:uint = 0; i < MovieClip(root).actions.length; i++)
			{
				//trace ("Action " + i +" ready: " + MovieClip(root).actions[i].name);
				var actionBtn = new ActionBtn (MovieClip(root).actions[i]);
				actionBtn.name = "actionBtn_" + i;
				actionBtn.x = 0;
				actionBtn.y = i * Y_STEEP;
				addChild (actionBtn);
				actionBtns[i] = actionBtn;
			}
		}
		
	}

}