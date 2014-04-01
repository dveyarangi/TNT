package org.tnt.multiplayer;


public interface IAvatar
{

	void putAction( IAvatarAction action );

	void gameAcknowledged();

	void putUpdate( IAvatarUpdate update );

}
