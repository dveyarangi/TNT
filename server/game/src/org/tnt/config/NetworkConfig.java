package org.tnt.config;

import javax.inject.Singleton;

@Singleton
public class NetworkConfig
{
	private final int port = 4242;
	
	public int getPort() { return port; }
}
