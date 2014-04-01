package org.tnt.multiplayer.network.hub;

import org.tnt.account.Player;
import org.tnt.multiplayer.IHub;

/**
 * Abstract client message.
 * All client messages class names are expected to start with "<package>.MC" preffix.
 * This allows to construct message class name from a shorter and nicer "type" field value.
 * 
 * @author fimar
 */
public abstract class IClientMessage
{

	/**
	 * Message type as it should be specified in JSON
	 * Auto-detecting message concrete instance type name:
	 */
	private final String type = this.getClass().getSimpleName().substring( 2 );

	/**
	 * Executes player management logic associated with this message.
	 * 
	 * @param player
	 * @param orchestrator
	 */
	abstract void process( Player player, IHub hub ) throws HubException;

	
	@Override
	public String toString() { return "ADMMSG: " + type; }

}
