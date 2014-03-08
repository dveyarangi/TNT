package org.tnt.game;

import java.util.HashMap;
import java.util.Map;

import org.reflections.Reflections;

import yarangi.java.ReflectionUtil;

import com.spinn3r.log5j.Logger;

public class GameFactory
{
	private static final Logger log = Logger.getLogger(GameFactory.class);
	private final Map <String, IGamePlugin> games = new HashMap <String, IGamePlugin> ();
	
	static GameFactory factory; 
	

	public static IGamePlugin getPlugin( String type )
	{
		return factory.games.get( type );
	}

	public static void init()
	{
		log.info("Loading server assets...");
		factory = new GameFactory();
		Reflections reflections = new Reflections();
		
		for( Class plugunClass : reflections.getTypesAnnotatedWith( GamePlugin.class ) )
		{
			
			IGamePlugin plugin = ReflectionUtil.createInstance( plugunClass );
			
			factory.registerPlugin( plugin );
			
			log.debug("Registered game plugin [" + plugin.getName() + "].");
	//		reflections.
		}
			
	}
	
	private void registerPlugin(IGamePlugin plugin)
	{
		games.put( plugin.getName(), plugin );
	}

}
