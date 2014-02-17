package org.tnt.game;

import io.netty.channel.Channel;

import org.tnt.GameType;
import org.tnt.IGameSimulator;
import org.tnt.game.rats.RatsHandler;
import org.tnt.game.rats.RatsSimulation;
import org.tnt.multiplayer.MultiplayerGame;
import org.tnt.multiplayer.realtime.IngameProtocolHandler;

public class GameFactory
{
	public IGameSimulator getSimulation(GameType type)
	{
		if(type == null)
			throw new IllegalArgumentException("Game type cannot be null");
		
		switch(type)
		{
		case RAT_RACE:
			return new RatsSimulation();
		}
		
		throw new IllegalArgumentException("Unknown game type " + type);
	}
	
	public IngameProtocolHandler getIngameHandler(Channel channel, MultiplayerGame game, int pid)
	{
		GameType type = game.getType();
		if(type == null)
			throw new IllegalArgumentException("Game type cannot be null");
		
		switch(type)
		{
		case RAT_RACE:
			return new RatsHandler(channel, game, pid);
		}
		
		throw new IllegalArgumentException("Unknown game type " + type);
	}
}
