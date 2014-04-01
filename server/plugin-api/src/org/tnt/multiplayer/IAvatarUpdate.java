package org.tnt.multiplayer;

import org.tnt.network.realtime.IServerPacket;
import org.tnt.plugins.GameSimulator;


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
