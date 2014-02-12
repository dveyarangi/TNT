package org.tnt.game;

import org.tnt.GameType;
import org.tnt.IGameSimulator;
import org.tnt.game.rats.RatsSimulation;

public class SimulatorFactory
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
}
