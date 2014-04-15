package org.tnt.account;


public interface IPlayer {

	public ICharacter getCharacter(final int charId);

	public int getCharactersAmount();

	public long getId();

	public void addCharacter(ICharacter character);
}
