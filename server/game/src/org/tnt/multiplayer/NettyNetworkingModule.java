package org.tnt.multiplayer;

import javax.inject.Singleton;

import org.tnt.INetworkThread;
import org.tnt.multiplayer.network.NettyNetwork;

import com.google.inject.AbstractModule;

public class NettyNetworkingModule extends AbstractModule
{

	@Override
	protected void configure()
	{
		bind( INetworkThread.class ).to(NettyNetwork.class).in( Singleton.class );
	}

}
