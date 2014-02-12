package org.tnt.multiplayer;

import io.netty.channel.ChannelFuture;

public class CharacterHandle
{
	
	private Character character;
	private ChannelFuture future;
	public CharacterHandle( Character character, ChannelFuture future )
	{
		super();
		this.character = character;
		this.future = future;
	}
	
	public Character getCharacter()
	{
		return character;
	}
	public ChannelFuture getFuture()
	{
		return future;
	}
	
	
}
