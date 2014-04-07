package org.tnt.multiplayer.network.hub;

import org.tnt.network.hub.IServerMessage;

/**
 * This message is sent by server to inform clients that the game is ready and is going to be started.

 * @author fimar
 */
public enum MSGo implements IServerMessage
{
	GO
}