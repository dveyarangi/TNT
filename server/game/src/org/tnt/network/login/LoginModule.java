package org.tnt.network.login;

import org.tnt.network.protocol.IAuthenticator;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class LoginModule extends AbstractModule
{

	@Override
	protected void configure()
	{

		bind( IAuthenticator.class )                .to( AuthHandler.class )         .in( Singleton.class);

	}

}
