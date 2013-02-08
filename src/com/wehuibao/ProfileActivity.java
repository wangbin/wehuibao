package com.wehuibao;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class ProfileActivity extends SherlockFragmentActivity {
	public static final String USERID = "userId";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.getSupportFragmentManager().findFragmentById(
				android.R.id.content) == null) {
			this.getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, new ProfileFragment()).commit();
		}
	}

}
