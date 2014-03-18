package org.tnt;

public interface IShutdownHook extends Runnable
{

	void fail(Exception e);

	void shutdown();

}
