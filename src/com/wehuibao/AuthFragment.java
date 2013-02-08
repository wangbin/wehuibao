package com.wehuibao;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class AuthFragment extends SherlockFragment {
	private WebView authWeb;
	private CookieSyncManager cookieSyncMAnager;
	private CookieManager cookieManager;
	public static final String AUTH_SERVICE = "AUTH_SERVICE";
	private static final String AUTH_OK_URL = "http://wehuibao.com/apiauthok";
	private static final String AUTH_URL = "http://wehuibao.com/apilogin/";
	public static final String BASE_URL = "http://wehuibao.com";

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

	private void goHome() {
		cookieSyncMAnager.sync();
		String cookie = cookieManager.getCookie(BASE_URL);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this.getActivity().getApplicationContext());
		prefs.edit().putString("cookie", cookie).commit();
		Intent intent = new Intent(this.getActivity(), DocListActivity.class);
		this.startActivity(intent);
	}

	class AuthClient extends WebViewClient {

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			if (url.equals(AUTH_OK_URL)) {
				goHome();
			}
		}
	}
}
