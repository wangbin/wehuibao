package com.wehuibao.json;

import com.google.gson.annotations.SerializedName;

public class Credential {
	@SerializedName("is_self")
	public Boolean isSelf;
	@SerializedName("id")
	public String userId;
	@SerializedName("name")
	public String name;

}
