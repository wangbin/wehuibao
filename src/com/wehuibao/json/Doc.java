package com.wehuibao.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Doc {
	public String abbrev_text;
	public String abbrev;
	public String absolute_url;
	public int cnt_follower;
	@SerializedName("id")
	public String docId;
	public String major_title;
	public int seqid;
	public Thumbnail thumb;
	public String title;
	public String url;
	public int vote_count;
	public List<User> sharers;
	
	public String get_absolute_url() {
		//TODO: should use absolute_url, like http://wehuibao.com/docr/ISG71x 
		return "http://wehuibao.com/doc/" + this.docId;
	}
	
	@Override
	public boolean equals(Object obj) {
		Doc doc = (Doc) obj;
		return this.docId.equals(doc.docId);
	}
}
