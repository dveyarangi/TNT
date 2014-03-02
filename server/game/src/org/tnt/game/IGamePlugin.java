package org.tnt.game;

import io.netty.channel.Channel;

import java.util.List;

import org.tnt.account.Character;
import org.tnt.multiplayer.MultiplayerGame;
import org.tnt.multiplayer.realtime.IngameProtocolHandler;

/**
 * This is interface for game factory for a specific game type.
 * The factory provides game server side simulation via {@link #createSimulation(List)}
 * and ingame protocol handler, optimized for the specific game type, via {@link #createIngameHandler(Channel, MultiplayerGame, int)}.
 * 
 * TODO: inject automatically using annotations
 * 
 * @author Fima
 *
 */
public interface IGamePlugin
{

	public String getName();
	
	public void init();
	
	public IGameSimulator createSimulation( List <Character> characters );
	
	public IngameProtocolHandler createNetworkCharacterDriver(Channel channel, MultiplayerGame game, int pid);
}
