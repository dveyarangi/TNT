package org.tnt.network;

import io.netty.channel.ChannelInitializer;

import org.tnt.account.IPlayerStore;
import org.tnt.halls.Halls;
import org.tnt.halls.IHalls;
import org.tnt.network.auth.AuthHandler;
import org.tnt.network.auth.IAuthenticator;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * This module configures networking services.
 * 
 * This module depends on Netty.
 * 
 * TODO: should depends on {@link IPlayerStore}
 * 
 * 
 * 
 * @author Fima
 *
 */
public class NetworkModule extends AbstractModule
{

	@Override
	protected void configure() {


		// binds hall services initializer
		bind( IHalls.class ).               to(Halls.class).in( Singleton.class);

		// connected players repository
		bind( IPlayerConnections.class )           .to( PlayerConnections.class )   .in( Singleton.class );

		// TODO:
		bind( ChannelInitializer.class )           .to( DummyChannelInitializer.class )         .in( Singleton.class );

		// server networking thread
		bind( INetworkThread.class )                .to( NettyNetwork.class )        .in( Singleton.class);
		// player authentication protocol
		// TODO: input trotttling, login monitoring,
		//

		bind( IAuthenticator.class )                .to( AuthHandler.class )         .in( Singleton.class);

		install(new FactoryModuleBuilder().build(ProtocolFactory.class));
	}

}
