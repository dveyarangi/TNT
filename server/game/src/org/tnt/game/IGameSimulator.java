package org.tnt.game;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tnt.account.Character;
import org.tnt.multiplayer.ICharacterAction;
import org.tnt.multiplayer.IGameUpdate;

public interface IGameSimulator
{
	
	public GameType getType();
	
	public void setCharacters( List <Character> characters );
	
	public void init();
	
	/**
	 * Steps game one tick forward; 
	 * @param time
	 * @return false is the game is over
	 */
	public void step(long time, TIntObjectHashMap <IGameUpdate> updates);
	
	public boolean isOver();

	public int getMaxCapacity();

	public void addCharacterAction( int pid, ICharacterAction action );

	public IGameUpdate getCharacterUpdate( int pid );

}
