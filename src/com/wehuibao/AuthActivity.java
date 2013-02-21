package com.wehuibao;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class AuthActivity extends SherlockFragmentActivity {
	
	public static final String AUTH_SERVICE_NAME = "AUTH_SERVICE_NAME";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String authServiceName = getIntent().getStringExtra(AUTH_SERVICE_NAME);
		this.getSupportActionBar().setTitle(getString(R.string.bind_to) + authServiceName);
		super.onCreate(savedInstanceState);
		if (this.getSupportFragmentManager().findFragmentById(
				android.R.id.content) == null) {
			this.getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, new AuthFragment()).commit();
		}
	}
}
