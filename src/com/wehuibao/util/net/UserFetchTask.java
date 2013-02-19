package com.wehuibao.util.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;
import com.wehuibao.json.AuthList;

import android.os.AsyncTask;

public class UserFetchTask extends AsyncTask<String, Void, AuthList> {

	@Override
	protected AuthList doInBackground(String... urls) {
		try {
			URL url = new URL(urls[0]);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setReadTimeout(5000);
			connection.setRequestMethod("GET");
			updateConnection(connection);
			connection.connect();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
			Gson gson = new Gson();
			AuthList authList = gson.fromJson(reader, AuthList.class);
			reader.close();
			updateAuthList(authList);
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

	public void updateAuthList(AuthList authList) {
		
	}

	public void updateConnection(HttpURLConnection connection) {
		
	}
}