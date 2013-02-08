package com.wehuibao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.gson.Gson;
import com.wehuibao.json.AuthList;

public class ProfileFragment extends SherlockFragment {
	private AuthList authList = null;
	private TextView profileName;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.profile, container, false);
		Intent intent = this.getActivity().getIntent();
		String userId = intent.getStringExtra(ProfileActivity.USERID);
		profileName = (TextView) view.findViewById(R.id.profileName);
		if (authList == null) {
			String url = "http://wehuibao.com/api/user/" + userId;
			new FetchUserTask().execute(url);
		}
		return view;
	}
	
	class FetchUserTask extends AsyncTask<String, Void, AuthList> {

		@Override
		protected AuthList doInBackground(String... urls) {
			try {
				URL url = new URL(urls[0]);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setReadTimeout(5000);
				connection.setRequestMethod("GET");
				connection.connect();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				Gson gson = new Gson();
				AuthList authList = gson.fromJson(reader, AuthList.class);
				return authList;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(AuthList authList) {
			ProfileFragment.this.authList = authList;
			profileName.setText(authList.name);
		}
	}
}
