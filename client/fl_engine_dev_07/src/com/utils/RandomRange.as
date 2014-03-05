package com.utils
{
	
	/**
	 * ...
	 * @author Roman Rozanoff
	 */
	public class RandomRange
	{
		
		public static function uintRandom(maxNum:Number, minNum:Number = 0):uint
		{
			return (Math.floor(Math.random() * (maxNum - minNum + 1)) + minNum);
		}
		
		public static function intRandom(rangeNum):int
		{
			var s:uint = uintRandom(10);
			if (s < 5)
			{
				return (Math.floor(Math.random() * (rangeNum + 1)));
			}
			else
			{
				return (-(Math.floor(Math.random() * (rangeNum + 1))));
			}
		}
	
	}

}