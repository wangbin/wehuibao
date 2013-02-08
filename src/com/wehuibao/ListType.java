package com.wehuibao;

public enum ListType {
	HOT("hot"),
	ME("me"),
	OTHER("other");
	
	private String listType;

	ListType(String listType) {
		this.listType = listType;
	}
	
	public String toString() {
		return this.listType;
	}
	
	public static ListType getListType(String typeName) {
		for (ListType lt : ListType.values()) {
			if (lt.toString().equals(typeName)) {
				return lt;
			}
		}
		return OTHER;
	}
}