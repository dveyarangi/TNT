package org.tnt.multiplayer.realtime;

import org.tnt.multiplayer.IGameResults;
import org.tnt.multiplayer.MultiplayerGame;

public interface IMultiplayerGameListener
{

	public void gameOver( MultiplayerGame multiplayerGame, IGameResults results );

}
