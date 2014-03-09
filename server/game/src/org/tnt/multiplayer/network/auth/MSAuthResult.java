package org.tnt.multiplayer.network.auth;

import com.google.gson.annotations.SerializedName;

/**
 * Authentication result message sent to client.
 * 
 * @author fimar
 */
public enum MSAuthResult
{
	@SerializedName("0") OK,
	@SerializedName("1") FAILED_UNKNOWN_PLAYER,
	@SerializedName("2") FAILED_INVALID_CREDENTIALS,
	@SerializedName("3") FAILED_ALREADY_LOGGED_IN,
	@SerializedName("255") FAILED_SERVER_ERROR;

	/**
	 * @return true if client is successfully authenticated. 
	 */
	public boolean isOk() {	return this == OK; }
}
