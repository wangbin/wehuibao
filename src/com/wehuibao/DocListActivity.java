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
	public static final String MENU_ID = "MENU_ID";
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
		Bundle fragmentArgs = new Bundle();
		
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
			fragmentArgs.putString(LIST_URL, DOC_LIST_URL + ME);
			fragmentArgs.putInt(MENU_ID, R.menu.me);
			
			getSupportActionBar().setTitle(getString(R.string.menu_home));
			break;
		case OTHER:
			userId = listType;
			fragmentArgs.putString(LIST_URL, DOC_LIST_URL + userId);
			fragmentArgs.putInt(MENU_ID, R.menu.doc_list);
			new FetchUserTask().execute(USER_URL + userId);
			break;
		default:
			fragmentArgs.putString(LIST_URL, HOT_URL);
			fragmentArgs.putInt(MENU_ID, R.menu.hot);
			this.getSupportActionBar().setTitle(R.string.menu_hot);
		}
		
		DocListFragment docListFragment = new DocListFragment();
		docListFragment.setArguments(fragmentArgs);
		
		if (this.getSupportFragmentManager().findFragmentById(
				android.R.id.content) == null) {
			this.getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, docListFragment).commit();
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
