package org.tnt.multiplayer;

import org.tnt.game.IGamePlugin;

public interface IGameFactory
{

	void init();

	IGamePlugin getPlugin( String gameType );

}
