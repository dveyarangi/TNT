package org.tnt.multiplayer;

import org.tnt.plugins.IGamePlugin;

/**
 * Game room for characters to wait for additional participants and game start.
 * 
 * Manages modifiable mapping of players to selected ingame characters;
 * 
 * @author fimar
 */
public class GameRoom extends HubRoom
{
	
	/**
	 * Room creation time
	 * TODO: use to monitor rooms that are pending for too long.
	 */
	private final long creationTime = System.currentTimeMillis();
	
	/**
	 * room capacity
	 */
	private final int capacity;
	
	/**
	 * Game served by this room
	 */
	private final IGamePlugin plugin;


	public GameRoom( IGamePlugin plugin, int capacity )
	{
		this.plugin = plugin;
		this.capacity = capacity;
	}

	
	
	public boolean isFull()
	{
		return getParticipants().size() >= capacity;
	}

	public String getType()	{ return plugin.getName(); }

	public IGamePlugin getPlugin() { return plugin; }


}
