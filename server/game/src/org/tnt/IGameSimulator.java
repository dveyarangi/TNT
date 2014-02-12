package org.tnt;

import java.util.List;
import org.tnt.account.Character;

public interface IGameSimulator
{
	
	public GameType getType();
	
	public void addCharacter( Character character );
	public boolean isFull();	
	
	public void init();
	
	/**
	 * Steps game one tick forward; 
	 * @param time
	 * @return false is the game is over
	 */
	public List <IGameUpdate> step(long time);
	
	public boolean isOver();

}
