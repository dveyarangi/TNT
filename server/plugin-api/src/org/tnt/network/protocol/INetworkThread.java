package org.tnt.network.protocol;

public interface INetworkThread extends Runnable
{

	void safeStop();

	void init();

}
