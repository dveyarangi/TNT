package org.tnt.game.rats;

import io.netty.buffer.ByteBuf;

import org.tnt.IGameUpdate;
import org.tnt.account.Character;
import org.tnt.multiplayer.realtime.IServerPacket;


/**
 * This is non-encoded message from server during the game session.
 * 
 * The message describes player character state
 * 
 * @author Fima
 *
 */
public class ServerPacket implements IGameUpdate
{
	/**
	 * Id of the player this packet addresses to
	 */
	int pid;
	
	/**
	 * player state time
	 */
	int time;
	
	/**
	 * character location
	 */
	float position;
	
	/**
	 * character current action; if not null it represent the beginning of the action
	 */
	int action;
	
	public ServerPacket(int pid, int time, float position, CharacterAction action)
	{
		this.pid = pid;
		this.time = time;
		this.position = position;
		this.action = action == null ? 0 : action.ordinal();
		
	}
	
	public int getPacketSize()
	{
		return 10;
	}

	@Override
	public void write( ByteBuf out )
	{
		out.writeByte( pid );
		out.writeInt( time );
		out.writeFloat( position );
		out.writeByte( action );

	}

	@Override
	public int getPID()
	{
		return pid;
	}


	
}
