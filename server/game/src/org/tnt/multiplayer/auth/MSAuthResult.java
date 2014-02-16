package org.tnt.multiplayer.auth;

import com.google.gson.annotations.SerializedName;

public enum MSAuthResult
{
	@SerializedName("0") OK,
	@SerializedName("1") FAILED_UNKNOWN_PLAYER,
	@SerializedName("2") FAILED_INVALID_CREDENTIALS,
	@SerializedName("3") FAILED_ALREADY_LOGGED_IN,
	@SerializedName("255") FAILED_SERVER_ERROR;

	public boolean isOk() {	return this == OK; }
	
	
}
