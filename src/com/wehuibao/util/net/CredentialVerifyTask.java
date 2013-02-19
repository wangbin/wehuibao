package com.wehuibao.util.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;
import com.wehuibao.json.Credential;

import android.os.AsyncTask;

public class CredentialVerifyTask extends AsyncTask<String, Void, Credential> {

	private static final String VERIFY_URL = "http://wehuibao.com/api/verify_credentials";

	@Override
	protected Credential doInBackground(String... cookies) {
		try {
			String cookie = cookies[0];
			URL url = new URL(VERIFY_URL);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setReadTimeout(5000);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Cookie", cookie);
			connection.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			Gson gson = new Gson();
			Credential credential = gson.fromJson(reader, Credential.class);
			reader.close();
			return credential;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
