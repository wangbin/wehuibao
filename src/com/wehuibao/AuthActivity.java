package com.wehuibao;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class AuthActivity extends SherlockFragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.getSupportFragmentManager().findFragmentById(
				android.R.id.content) == null) {
			this.getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, new AuthFragment()).commit();
		}
	}
}
