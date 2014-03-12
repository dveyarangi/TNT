package org.tnt;

import com.google.inject.Singleton;
@Singleton
public class Calculator implements ICalculator
{

	@Override
	public long getTime()
	{
		return System.nanoTime();
	}
}
