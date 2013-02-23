package com.wehuibao;

public enum AuthService {
	SINA("sina2", R.string.sina),
	QQ("qq", R.string.qq), 
	DOUBAN("douban", R.string.douban), 
	FANFOU("fanfou", R.string.fanfou);

	private String service_id;
	private int serviceNameId;

	AuthService(String service_id, int serviceNameId) {
		this.service_id = service_id;
		this.serviceNameId = serviceNameId;
	}

	public String toString() {
		return this.service_id;
	}
	
	public int getServiceNameId() {
		return this.serviceNameId;
	}

	public static AuthService getAuthService(String service_id) {
		for (AuthService authService : AuthService.values()) {
			if (authService.service_id.equals(service_id)) {
				return authService;
			}
		}
		return FANFOU;
	}
}
