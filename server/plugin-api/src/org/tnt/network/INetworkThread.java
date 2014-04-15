package org.tnt.network;

public interface INetworkThread extends Runnable
{

	void safeStop();

	void init();

}
