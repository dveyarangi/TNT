package org.tnt.account;

import com.google.common.base.Preconditions;


public class Character
{
	private Player player;
	
	public Character(Player player)
	{
		Preconditions.checkNotNull( player );
	}
	public Player getPlayer()
	{
		return player;
	}
}
