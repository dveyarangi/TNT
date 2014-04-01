package org.tnt.multiplayer;

import org.tnt.plugins.IGamePlugin;

public interface IGameFactory
{

	void init();

	IGamePlugin getPlugin( String gameType );

}
