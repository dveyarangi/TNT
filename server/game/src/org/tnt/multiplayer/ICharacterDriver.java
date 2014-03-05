package org.tnt.multiplayer;

/**
 * Interface for talking to ingame character behavior implementation.
 * 
 * @author Fima
 */
public interface ICharacterDriver
{

	void update( IGameUpdate update );

	void stop();

	void setStarted( IGameUpdate update );

}
