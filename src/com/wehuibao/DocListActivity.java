package com.wehuibao;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class DocListActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.getSupportFragmentManager().findFragmentById(
				android.R.id.content) == null) {
			this.getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, new DocListFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.doc_list, menu);
		return false;
	}
}
