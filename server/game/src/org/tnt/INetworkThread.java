package org.tnt;

public interface INetworkThread extends Runnable
{

	void safeStop();

	void init();

}
