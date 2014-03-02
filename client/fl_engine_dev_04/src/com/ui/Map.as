package com.ui
{
	import flash.display.MovieClip;
	import flash.display.Sprite;
	import flash.text.TextField;
	
	/**
	 * ...
	 * @author Roman Rozanoff
	 */
	public class Map extends MovieClip implements Iui
	{
		public var current_txt:TextField;
		public var total_txt:TextField;
		
		public var view:MovieClip;
		public var masker:Sprite;
		
		public function Map()
		{
		
		}
		
		public function setup():void
		{
			view.mask = masker;
			reset();
		}
		
		public function reset():void
		{
			view.track.height = MovieClip(root).distance + 800;
			view.track.y = 0;

			view.finishLine.y = MovieClip(root).distance;
			view.player.y = 0;
			view.player.gotoAndStop(1);
			
			current_txt.text = "0";
			total_txt.text = "0";
		}
		
		public function update():void
		{
			current_txt.text = Math.round(MovieClip(root).engine.currentPosition).toString();
			total_txt.text = MovieClip(root).distance.toString();
			
			view.track.y = MovieClip(root).engine.currentPosition;
			view.finishLine.y = - MovieClip(root).distance + MovieClip(root).engine.currentPosition;
			
			if (MovieClip(root).engine.currentSpeed > 0)
			{
				view.player.play()
			}
			else
			{
				view.player.stop()
			}
		}
	
	}

}