package org.tnt.multiplayer.auth;

import com.google.gson.annotations.SerializedName;

public enum MSAuthResult
{
	@SerializedName("0") OK,
	@SerializedName("1") FAILED_UNKNOWN_PLAYER,
	@SerializedName("2") FAILED_INVALID_CREDENTIALS,
	@SerializedName("3") FAILED_SERVER_ERROR;
	
	
}
