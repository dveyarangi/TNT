package org.tnt.hub;

public class HubException extends Exception
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1659844592973771596L;

	public HubException(Exception e) { super(e); }
	public HubException(String msg) { super(msg); }
}
