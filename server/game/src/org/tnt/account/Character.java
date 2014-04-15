package org.tnt.account;

import com.google.common.base.Preconditions;


public class Character implements ICharacter
{
	private long id;

	private IPlayer player;

	public Character(final long id, final IPlayer player)
	{
		this.player = Preconditions.checkNotNull( player );

		this.id = id;
	}
	public IPlayer getPlayer() { return player; }
	public long getId() { return id; }
}
