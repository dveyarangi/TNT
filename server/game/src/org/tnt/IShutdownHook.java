package org.tnt;

public interface IShutdownHook extends Runnable
{

	void fail();

	void shutdown();

}
