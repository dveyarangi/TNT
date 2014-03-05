package com
{
	import com.events.CharacterEvent;
	import com.events.GameEvent;
	import com.events.UiEvent;
	import flash.display.MovieClip;
	import flash.events.Event;
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	
	/**
	 * ...
	 * @author Roman Rozanoff
	 */
	public class GameEngine extends MovieClip
	{
		
		public static const UPDATE_GAME_TIME:uint = 10;
		
		public var currentPosition:Number = 0; /// Curren position of the Player
		public var currentSpeed:Number = 0; /// Current Speed of the Player
		public var currentTime:Number = 0; /// Current Time of the Player
		
		private var updateGameTimer:Timer = new Timer(UPDATE_GAME_TIME);
		private var updateDisplayTimer:Timer = new Timer(300);

		
		private var go:Boolean = false;
		private var finish:Boolean = false;
		
		public function GameEngine()
		{
		
		}
		
		public function setupNewGame():void
		{
			stage.addEventListener(GameEvent.START, onGameStart);
			stage.addEventListener(GameEvent.PLAYER_START_RUN, onPlayerStart);
			go = false;
		
		}
		
		private function onGameStart(e:GameEvent):void
		{
			trace("Engine: gameStarted!!!")
			currentPosition = 0;
			currentSpeed = 0;
			currentTime = 0;
			go = true;
			finish = false;
			startDisplayUpdate();
		}
		
		private function onPlayerStart(e:GameEvent):void
		{
			if (go)
			{
				trace("Engine: start runnining!!!!");
				MovieClip(root).charState = "acelerate";
				stage.addEventListener (GameEvent.PLAYER_HIT_OBS, onPlayerHitsObject)
				stage.dispatchEvent(new CharacterEvent(CharacterEvent.CHANGE_STATE));
				startGameUpdate();
			}
			else
			{
				trace("Engine: FALSH START!!!!")
				stage.dispatchEvent(new GameEvent(GameEvent.FALSE_START));
				stopDisplayUpdate();
			}
		
		}
		
		
		
		private function updateGame(e:TimerEvent):void
		{
			switch (MovieClip(root).charState)
			{
				case "wait": 
					currentSpeed = 0;
					break;
				
				case "acelerate": 
					currentSpeed += (MovieClip(root).acseleration);
					if (currentSpeed > MovieClip(root).topSpeed)
					{
						currentSpeed = MovieClip(root).topSpeed
						MovieClip(root).charState = "run";
						stage.dispatchEvent(new CharacterEvent(CharacterEvent.CHANGE_STATE));
					}
					break;
				
				case "break": 
					currentSpeed -= (MovieClip(root).acseleration);
					if (currentSpeed < MovieClip(root).minSpeed)
					{
						if (!finish) {
							MovieClip(root).topSpeed = MovieClip(root).rat.TOP_SPEED;
							MovieClip(root).minSpeed = MovieClip(root).rat.MIN_SPEED;
							MovieClip(root).acseleration = MovieClip(root).rat.ACSELERATION;
						}
						currentSpeed = MovieClip(root).minSpeed
						MovieClip(root).charState = "acelerate";
						stage.dispatchEvent(new CharacterEvent(CharacterEvent.CHANGE_STATE));
					}
					break;

			}

			currentPosition += (currentSpeed / 100);
			
			if (currentPosition >= MovieClip(root).distance)
			{
				if (!finish)
				{
					stage.dispatchEvent(new GameEvent(GameEvent.PLAYER_FINISHED));
					MovieClip(root).minSpeed = 0;
					MovieClip(root).acseleration = 2;
					MovieClip(root).charState = "break";
					stage.dispatchEvent(new CharacterEvent(CharacterEvent.CHANGE_STATE));
					MovieClip(root).finalPlayerTime = currentTime;
					currentPosition = MovieClip(root).distance;
					updateUiDisplay();
					stopDisplayUpdate();
					finish = true;
				}
				
				if (finish && currentSpeed <= 0)
				{
					stopGameUpdate();
					stage.removeEventListener (GameEvent.PLAYER_HIT_OBS, onPlayerHitsObject)
					stage.dispatchEvent(new GameEvent(GameEvent.END));
					MovieClip(root).charState = "wait";
					stage.dispatchEvent(new CharacterEvent(CharacterEvent.CHANGE_STATE));
				}
				
			}
		}
		
		private function updateUiDisplay(e:TimerEvent = null):void
		{
			currentTime += (UPDATE_GAME_TIME / 100);
			stage.dispatchEvent(new UiEvent(UiEvent.UPDATE_UI));
		}
		
		private function onPlayerHitsObject(e:GameEvent):void 
		{
			MovieClip(root).acseleration = 10;
			MovieClip(root).charState = "break";
			stage.dispatchEvent(new CharacterEvent(CharacterEvent.CHANGE_STATE));
		}
		
		//////////////////////////////////////////////////////////////////////////////////////////// START STOP TIMERS
		private function startGameUpdate():void
		{
			updateGameTimer.reset();
			updateGameTimer.addEventListener(TimerEvent.TIMER, updateGame);
			updateGameTimer.start();
		}
		
		private function stopGameUpdate():void
		{
			updateGameTimer.stop();
			updateGameTimer.removeEventListener(TimerEvent.TIMER, updateGame);
		}
		
		private function startDisplayUpdate():void
		{
			updateDisplayTimer.reset();
			updateDisplayTimer.addEventListener(TimerEvent.TIMER, updateUiDisplay);
			updateDisplayTimer.start();
		}
		
		private function stopDisplayUpdate():void
		{
			updateDisplayTimer.stop();
			updateDisplayTimer.removeEventListener(TimerEvent.TIMER, updateUiDisplay);
		
		}
		//////////////////////////////////////////////////////////////////////////////////////////// START STOP TIMERS
	
	}

}