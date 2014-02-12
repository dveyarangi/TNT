package org.tnt.multiplayer.realtime;

import org.tnt.game.rats.CharacterAction;

/**
 * This is non-encoded message from server during the game session.
 * 
 * The message describes player character state
 * 
 * @author Fima
 *
 */
public class ServerPacket
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
	 * character location
	 */
	float x, y;
	
	/**
	 * character current action; if not null it represent the beginning of the action
	 */
	CharacterAction action;

}
