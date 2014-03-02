package org.tnt.util;

import yarangi.ids.IDUtil;

/**
 * Centralizes various object id generation
 * 
 * @author Fima
 */
public class IDProvider
{
	private static final int GAME_ID_LEN = 32;
	public static String generateGameId() { return IDUtil.generateId( GAME_ID_LEN ); }
}
