package org.tnt.multiplayer.admin;

public abstract class IAdminMessageHandler <M extends IClientMessage>

{
	public abstract void handle(M message);
}
