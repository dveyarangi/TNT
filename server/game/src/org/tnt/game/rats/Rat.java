package org.tnt.game.rats;

public class Rat
{
	private Character character;
	
	private float position;
	
	private CharacterAction currentAction;
	
	private int actionTime;
	
	private float speed;
	
	
	public Rat(Character character)
	{
		this.character = character;
		
		this.position = 0;
		
		this.currentAction = null;
		
		this.actionTime = 0;
	}
	
	public void update(float position)
	{
		this.position = position;
	}


	public Character getCharacter() { return character; }

	public float getPosition() { return position; }

	public CharacterAction getCurrentAction() { return currentAction; }

	public int getActionTime() { return actionTime; }
	
	
	
}
