package org.tnt.multiplayer;




/**
 * This might be the place to put bot's in-game scripted room actions and AI initiation. 
 * @author Fima
 *
 */
public class PlayerDummyDriver implements IPlayerDriver
{

	@Override
	public void gameRoomUpdated( GameRoom room )
	{
	}
	
	@Override
	public ICharacterDriver gameStarted( MultiplayerGame game, int pid )
	{
		return null;
	}

	@Override
	public void gameEnded( IGameResults results )
	{
	}


}
