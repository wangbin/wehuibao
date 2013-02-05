package com.wehuibao.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class AuthList {
	public List<Auth> auth_list;
	public String description;
	@SerializedName("id")
	public String userId;
	public Boolean is_self;
	public String name;
	public String profile_image_url;
}
