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
		
	}

}