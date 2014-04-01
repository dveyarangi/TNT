package org.tnt.plugins;

import org.tnt.multiplayer.IAvatarUpdate;



public interface IGameSimulator
{

	void step( long stepTime, int time );

	IGameResults isOver();

	void destroy();

	IAvatarUpdate getStartingUpdate( int pid );

}
