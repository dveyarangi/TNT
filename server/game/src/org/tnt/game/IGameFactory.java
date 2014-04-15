package org.tnt.game;


public interface IGameFactory
{

	void init();

	IGamePlugin getPlugin( String gameType );

}
