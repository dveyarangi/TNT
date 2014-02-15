package org.tnt;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tnt.account.Character;

public interface IGameSimulator
{
	
	public GameType getType();
	
	public void setCharacters( Map<Character, Integer> characters );
	
	public void init();
	
	/**
	 * Steps game one tick forward; 
	 * @param time
	 * @return false is the game is over
	 */
	public List <IGameUpdate> step(long time);
	
	public boolean isOver();

	public int getMaxCapacity();

}
