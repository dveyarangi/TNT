package org.tnt.account;

public interface IPlayerStore
{

	void init();

	IPlayer getPlayer( long playerId );

}
