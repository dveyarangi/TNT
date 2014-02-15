package org.tnt.account;

import com.google.common.base.Preconditions;


public class Character
{
	private long id;
	
	private Player player;
	
	public Character(Player player)
	{
		Preconditions.checkNotNull( player );
	}
	public Player getPlayer() { return player; }
	public long getId() { return id; }
}
