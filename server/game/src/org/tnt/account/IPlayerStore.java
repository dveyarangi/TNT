package org.tnt.account;

public interface IPlayerStore
{

	void init();

	Player getPlayer( long playerId );

}
