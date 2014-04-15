package org.tnt.game;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class GameModule extends AbstractModule
{

	@Override
	protected void configure() {

		// utility calculator
		bind( ICalculator.class ).          to( Calculator.class )          .in( Singleton.class);

		// game resources provider
		bind( IGameFactory.class ).         to( GameFactory.class )         .in( Singleton.class);
	}

}
