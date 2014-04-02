package org.tnt.multiplayer;

import org.tnt.multiplayer.network.hub.MSClose;
import org.tnt.multiplayer.realtime.Avatar;
import org.tnt.plugins.IGameResults;

/**
 * This might be the place to put bot's in-game scripted room actions and AI initiation. 
 * @author Fima
 *
 */
public class PlayerDummyDriver implements IPlayerHubDriver
{

	@Override
	public void gameRoomUpdated( GameRoom room )
	{
	}


	@Override
	public void gameEnded( IGameResults results )
	{
	}


	@Override
	public IAvatarDriver playerInGame( GameRoom room, Avatar avatar )
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void playerInHub()
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void stop( MSClose reason )
	{
		// TODO Auto-generated method stub
		
	}



}
