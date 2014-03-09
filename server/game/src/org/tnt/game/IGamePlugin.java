package org.tnt.game;

import io.netty.channel.Channel;

import java.util.List;

import org.tnt.multiplayer.network.realtime.IngameProtocolHandler;
import org.tnt.multiplayer.realtime.Arena;
import org.tnt.multiplayer.realtime.Avatar;

/**
 * This is interface for game factory for a specific game type.
 * The factory provides game server side simulation via {@link #createSimulation(List)}
 * and ingame protocol handler, optimized for the specific game type, via {@link #createIngameHandler(Channel, Arena, int)}.
 * 
 * TODO: inject automatically using annotations
 * 
 * @author Fima
 *
 */
public interface IGamePlugin
{
	/**
	 * Just a name
	 */
	public String getName();

	/**
	 * Load resources
	 */
	public void init();
	
	/**
	 * Create game simulator
	 * @param game
	 * @return
	 */
	public GameSimulator createSimulation( Arena game );
	
	/**
	 * Create game network driver
	 * @param channel
	 * @param game
	 * @param pid
	 * @return
	 */
	public IngameProtocolHandler createNetworkCharacterDriver(Channel channel, Avatar avatar);
}
