package org.tnt.multiplayer;

import org.tnt.game.GameSimulator;
import org.tnt.network.realtime.IServerPacket;


/**
 * Update of {@link Avatar} state from {@link GameSimulator}.
 * 
 * Concrete implementation should also implement {@link IServerPacket},
 * to allow network serialization.
 * 
 * @author Fima
 */
public interface IAvatarUpdate
{
}
