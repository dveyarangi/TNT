package org.tnt.account;

import com.google.common.base.Preconditions;


public class Character
{
	private long id;
	
	private Player player;
	
	public Character(long id, Player player)
	{
		this.player = Preconditions.checkNotNull( player );
		
		this.id = id;
	}
	public Player getPlayer() { return player; }
	public long getId() { return id; }
}
