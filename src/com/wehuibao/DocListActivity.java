package com.wehuibao;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.wehuibao.json.AuthList;
import com.wehuibao.util.net.UserFetchTask;

public class DocListActivity extends SherlockFragmentActivity {
	public static final String LIST_TYPE = "LIST_TYPE";
	public static final String LIST_URL = "LIST_URL";
	private static final String ME = "@me";
	private static final String USER_URL = "http://wehuibao.com/api/user/";
	static final String HOT_URL = "http://wehuibao.com/api/hot/";
	private static final String DOC_LIST_URL = "http://wehuibao.com/api/doclist/";
	private ListType lt;
	private String userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String cookie = prefs.getString("cookie", null);
		Intent intent = getIntent();
		String listType = intent.getStringExtra(LIST_TYPE);
		if (listType != null) {
			lt = ListType.getListType(listType);
		} else {
			if (cookie != null) {
				lt = ListType.ME;
			} else {
				lt = ListType.HOT;
			}
		}

		switch (lt) {
		case ME:
			userId = "@me";
			savedInstanceState.putString(LIST_URL, DOC_LIST_URL + ME);
			getSupportActionBar().setTitle(getString(R.string.menu_home));
			break;
		case OTHER:
			userId = listType;
			savedInstanceState.putString(LIST_URL, DOC_LIST_URL + userId);
			new FetchUserTask().execute(USER_URL + userId);
			break;
		default:
			savedInstanceState.putString(LIST_URL, HOT_URL);
			this.getSupportActionBar().setTitle(R.string.menu_hot);
		}

		if (this.getSupportFragmentManager().findFragmentById(
				android.R.id.content) == null) {
			this.getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, new DocListFragment()).commit();
		}
	}
	
	class FetchUserTask extends UserFetchTask {
		@Override
		public void onPostExecute(AuthList authList) {
			DocListActivity.this.getSupportActionBar().setTitle(
					authList.name + getString(R.string.user_home));
		}
	}
}
