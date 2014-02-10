package org.tnt.protocol.admin;

import org.tnt.GameType;

public class MGameRequest implements IAdminMessage
{
	private GameType gameType;
	
	private long characterId;

	public GameType getGameType()
	{
		return gameType;
	}

	public long getCharacterId()
	{
		return characterId;
	}
	
	
}
