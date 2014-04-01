package org.tnt.multiplayer;


public interface IAvatarDriver
{

	void update( IAvatarUpdate update );

	void stop();

	void setStarted( IAvatarUpdate update );

}
