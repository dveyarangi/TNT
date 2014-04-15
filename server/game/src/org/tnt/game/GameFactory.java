package org.tnt.game;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import org.reflections.Reflections;

import yarangi.java.ReflectionUtil;

import com.spinn3r.log5j.Logger;

@Singleton
public class GameFactory implements IGameFactory
{
	private final Logger log = Logger.getLogger(GameFactory.class);
	private final Map <String, IGamePlugin> games = new HashMap <String, IGamePlugin> ();
	
	public GameFactory() {}

	@Override
	public IGamePlugin getPlugin( String type )
	{
		return games.get( type );
	}

	@Override
	public void init()
	{
		log.info("Loading server assets...");
		Reflections reflections = new Reflections();
		
		for( Class plugunClass : reflections.getTypesAnnotatedWith( GamePlugin.class ) )
		{
			
			IGamePlugin plugin = ReflectionUtil.createInstance( plugunClass );
			
			registerPlugin( plugin );
			
			log.debug("Registered game plugin [" + plugin.getName() + "].");
	//		reflections.
		}
	}
	
	private void registerPlugin(IGamePlugin plugin)
	{
		games.put( plugin.getName(), plugin );
	}


}
