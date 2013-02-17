package com.wehuibao;

public enum AuthService {
	SINA("sina2"), QQ("qq"), DOUBAN("douban"), FANFOU("fanfou");

	private String service_id;

	AuthService(String service_id) {
		this.service_id = service_id;
	}

	public String toString() {
		return this.service_id;
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
