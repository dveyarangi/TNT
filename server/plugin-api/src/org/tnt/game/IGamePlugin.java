package org.tnt.game;

import io.netty.channel.Channel;

import org.tnt.ai.IAvatarAISettings;
import org.tnt.multiplayer.IArena;
import org.tnt.multiplayer.IAvatar;
import org.tnt.multiplayer.IAvatarDriver;
import org.tnt.realtime.IAvatarNetworker;

/**
 * This is interface for game factory for a specific game type.
 * The factory provides game server side simulation via {@link #createSimulation(IArena)}
 * and ingame protocol handler, optimized for the specific game type, via {@link #createAvatarNetworker(Channel, IAvatar)}.
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
	 * Create game simulator.
	 * 
	 * @param game
	 * @return
	 */
	public IGameSimulator createSimulation( IArena arena );
	
	/**
	 * Create game network driver
	 * @param channel
	 * @param game
	 * @param pid
	 * @return
	 */
	public IAvatarNetworker createAvatarNetworker(Channel channel, IAvatar avatar);
	
	public IAvatarDriver createAvatarAI( IAvatar avatar, IAvatarAISettings settings );
}
