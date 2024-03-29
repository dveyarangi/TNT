package org.tnt.hub;

import javax.inject.Singleton;

import org.tnt.multiplayer.network.NettyNetwork;
import org.tnt.network.protocol.INetworkThread;

import com.google.inject.AbstractModule;

public class NettyNetworkingModule extends AbstractModule
{

	@Override
	protected void configure()
	{
		bind( INetworkThread.class ).to(NettyNetwork.class).in( Singleton.class );
	}

}
