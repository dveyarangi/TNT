package org.tnt.game;

import java.util.HashMap;
import java.util.Map;

public class GameFactory
{
	private static final Map <String, IGamePlugin> games = new HashMap <String, IGamePlugin> ();
	
	public static void registerPlugin(IGamePlugin plugin)
	{
		games.put( plugin.getName(), plugin );
	}

	public static IGamePlugin getPlugin( String type )
	{
		return games.get( type );
	}
}
