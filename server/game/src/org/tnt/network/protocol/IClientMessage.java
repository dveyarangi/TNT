package org.tnt.network.protocol;

import org.tnt.account.Player;

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
	protected abstract void process( Player player );


	@Override
	public String toString() { return "ADMMSG: " + type; }

}
