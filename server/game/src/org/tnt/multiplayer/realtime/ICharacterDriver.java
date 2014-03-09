package org.tnt.multiplayer.realtime;

/**
 * Interface for talking to ingame character behavior implementation.
 * 
 * @author Fima
 */
public interface ICharacterDriver
{

	void update( IAvatarUpdate update );

	void stop();

	void setStarted( IAvatarUpdate update );

}
