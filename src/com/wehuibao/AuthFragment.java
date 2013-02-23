package com.wehuibao;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.wehuibao.json.Credential;
import com.wehuibao.util.net.CredentialVerifyTask;

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

	class AuthClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			if (url.equals(AUTH_OK_URL)) {
				cookieSyncMAnager.sync();
				String cookie = cookieManager.getCookie(BASE_URL);
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(getActivity()
								.getApplicationContext());
				prefs.edit().putString("cookie", cookie).commit();
				new VerifyCredentialsTask().execute(cookie);
			} else {
				super.onPageStarted(view, url, favicon);
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			Toast.makeText(getActivity(),
					getString(R.string.err_msg_cannot_connet),
					Toast.LENGTH_SHORT).show();
		}
	}

	class VerifyCredentialsTask extends CredentialVerifyTask {

		@Override
		protected void onPostExecute(Credential credential) {
			Intent intent = new Intent(getActivity(), ProfileActivity.class);
			if (credential != null) {
				intent.putExtra(ProfileActivity.USERID, credential.userId);
				intent.putExtra(ProfileActivity.USER_NAME, credential.name);
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(getActivity()
								.getApplicationContext());
				prefs.edit().putString("userId", credential.userId)
						.putString("userName", credential.name).commit();
				startActivity(intent);
			}
		}

	}
}
