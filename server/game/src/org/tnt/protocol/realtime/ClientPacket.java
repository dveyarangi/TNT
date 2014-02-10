package org.tnt.protocol.realtime;

import org.tnt.game.CharacterAction;

/**
 * This is decoded message from client during the game session
 * 
 * @author Fima
 */
public class ClientPacket
{
	/**
	 * Id of the player this packet addresses to
	 */
	int playerId;
	
	/**
	 * player state time
	 */
	int time;
	
	/**
	 * character current action;  
	 */
	CharacterAction action;
}
