package com.wehuibao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.gson.Gson;
import com.wehuibao.json.AuthList;
import com.wehuibao.json.Credential;

public class AuthFragment extends SherlockFragment {
	private WebView authWeb;
	private CookieSyncManager cookieSyncMAnager;
	private CookieManager cookieManager;
	public static final String AUTH_SERVICE = "AUTH_SERVICE";
	private static final String AUTH_OK_URL = "http://wehuibao.com/apiauthok";
	private static final String AUTH_URL = "http://wehuibao.com/apilogin/";
	private static final String VERIFY_URL = "http://wehuibao.com/api/verify_credentials";
	public static final String BASE_URL = "http://wehuibao.com";
	private String cookie;
	private Credential credential;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.auth, container, false);
		authWeb = (WebView) view.findViewById(R.id.authWebView);
		String authService = (String) this.getActivity().getIntent()
				.getExtras().get(AUTH_SERVICE);
		String url = AUTH_URL + authService;
		cookieSyncMAnager = CookieSyncManager
				.createInstance(this.getActivity());
		cookieManager = CookieManager.getInstance();
		authWeb.setWebViewClient(new AuthClient());
		authWeb.getSettings().setJavaScriptEnabled(true);
		authWeb.loadUrl(url);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		cookieSyncMAnager.startSync();
	}

	@Override
	public void onPause() {
		super.onPause();
		cookieSyncMAnager.stopSync();
	}

	class AuthClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			if (url.equals(AUTH_OK_URL)) {
				new VerifyCredentialsTask().execute();
			}
		}
	}

	class VerifyCredentialsTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			cookieSyncMAnager.sync();
			cookie = cookieManager.getCookie(BASE_URL);
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(getActivity()
							.getApplicationContext());
			prefs.edit().putString("cookie", cookie).commit();
			try {
				URL url = new URL(VERIFY_URL);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setReadTimeout(5000);
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Cookie", cookie);
				connection.connect();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				Gson gson = new Gson();
				credential = gson.fromJson(reader, Credential.class);
				reader.close();
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
		protected void onPostExecute(Void unUsed) {
			Intent intent = new Intent(getActivity(), ProfileActivity.class);
			if (credential != null) {
				intent.putExtra(ProfileActivity.USERID, credential.userId);
			}
			startActivity(intent);
		}

	}
}
