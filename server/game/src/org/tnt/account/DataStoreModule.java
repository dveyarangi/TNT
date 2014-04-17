package org.tnt.account;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class DataStoreModule extends AbstractModule
{

	@Override
	protected void configure() {
		// player data store
		bind( IPlayerStore.class ).         to( PlayerStore.class )         .in( Singleton.class);
	}

}
