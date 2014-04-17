package org.tnt.network.protocol;

import org.tnt.account.IPlayerStore;
import org.tnt.halls.Halls;
import org.tnt.halls.IHalls;

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
 * This module requires bindings for:
 * 
 * {@link IAuthenticator}
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

		// server networking thread
		bind( INetworkThread.class )                .to( NettyNetwork.class )        .in( Singleton.class);
		// player authentication protocol
		// TODO: input trotttling, login monitoring,
		//

		install(new FactoryModuleBuilder().build(ProtocolFactory.class));
	}

}
