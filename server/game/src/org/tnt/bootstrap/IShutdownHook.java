package org.tnt.bootstrap;

public interface IShutdownHook extends Runnable
{

	void fail(Exception e);

	void shutdown();

}
