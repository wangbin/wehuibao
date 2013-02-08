package com.wehuibao;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.gson.Gson;
import com.wehuibao.json.Auth;
import com.wehuibao.json.AuthList;

public class ProfileFragment extends SherlockFragment implements OnClickListener {
	private AuthList authList = null;
	private TextView profileName;
	private TextView profileDesc;
	private Button homeButton;
	private Button logoutButton;
	private TableLayout authServices;
	private String cookie;
	private static final String LOGOUT_URL = "http://wehuibao.com/api/logout";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.profile, container, false);
		Intent intent = this.getActivity().getIntent();
		String userId = intent.getStringExtra(ProfileActivity.USERID);
		profileName = (TextView) view.findViewById(R.id.profileName);
		profileDesc = (TextView) view.findViewById(R.id.profileDescription);
		authServices = (TableLayout) view.findViewById(R.id.auth_services);
		homeButton = (Button) view.findViewById(R.id.homeButton);
		logoutButton = (Button) view.findViewById(R.id.logout);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity()
						.getApplicationContext());
		cookie = prefs.getString("cookie", null);
		if (userId != null && authList == null) {
			String url = "http://wehuibao.com/api/user/" + userId;
			new FetchUserTask().execute(url);
		} else {
			buildBindingTable();
		}
		if (cookie != null) {
			logoutButton.setVisibility(View.VISIBLE);
			logoutButton.setOnClickListener(this);
		}
		return view;
	}

	private void buildBindingTable() {
		TableRow sinaRow = (TableRow) ProfileFragment.this.getActivity()
				.getLayoutInflater()
				.inflate(R.layout.auth_service_table_row, null);
		TextView authServiceName = (TextView) sinaRow
				.findViewById(R.id.auth_service_name);
		authServiceName.setText("����΢��");
		TextView authStatus = (TextView) sinaRow.findViewById(R.id.auth_status);
		authStatus
				.setText(Html
						.fromHtml("<a href=\"http://wehuibao.com/apilogin/sina2/\">��¼</a>"));
		sinaRow.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent authIntent = new Intent(getActivity(),
						AuthActivity.class);
				authIntent.putExtra(AuthFragment.AUTH_SERVICE, "sina2");
				startActivity(authIntent);

			}
		});
		authServices.addView(sinaRow, new TableLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		TableRow tencentRow = (TableRow) ProfileFragment.this.getActivity()
				.getLayoutInflater()
				.inflate(R.layout.auth_service_table_row, null);
		authServiceName = (TextView) tencentRow
				.findViewById(R.id.auth_service_name);
		authServiceName.setText("��Ѷ΢��");
		authStatus = (TextView) tencentRow.findViewById(R.id.auth_status);
		authStatus
				.setText(Html
						.fromHtml("<a href=\"http://wehuibao.com/apilogin/qq/\">��¼</a>"));
		tencentRow.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent authIntent = new Intent(getActivity(),
						AuthActivity.class);
				authIntent.putExtra(AuthFragment.AUTH_SERVICE, "qq");
				startActivity(authIntent);

			}
		});
		authServices.addView(tencentRow, new TableLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		TableRow doubanRow = (TableRow) ProfileFragment.this.getActivity()
				.getLayoutInflater()
				.inflate(R.layout.auth_service_table_row, null);
		authServiceName = (TextView) doubanRow
				.findViewById(R.id.auth_service_name);
		authServiceName.setText("����");
		authStatus = (TextView) doubanRow.findViewById(R.id.auth_status);
		authStatus
				.setText(Html
						.fromHtml("<a href=\"http://wehuibao.com/apilogin/douban/\">��¼</a>"));
		doubanRow.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent authIntent = new Intent(getActivity(),
						AuthActivity.class);
				authIntent.putExtra(AuthFragment.AUTH_SERVICE, "douban");
				startActivity(authIntent);

			}
		});
		authServices.addView(doubanRow, new TableLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		TableRow fanfouRow = (TableRow) ProfileFragment.this.getActivity()
				.getLayoutInflater()
				.inflate(R.layout.auth_service_table_row, null);
		authServiceName = (TextView) fanfouRow
				.findViewById(R.id.auth_service_name);
		authServiceName.setText("����");
		authStatus = (TextView) fanfouRow.findViewById(R.id.auth_status);
		authStatus
				.setText(Html
						.fromHtml("<a href=\"http://wehuibao.com/apilogin/fanfou/\">��¼</a>"));
		fanfouRow.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent authIntent = new Intent(getActivity(),
						AuthActivity.class);
				authIntent.putExtra(AuthFragment.AUTH_SERVICE, "fanfou");
				startActivity(authIntent);

			}
		});
		authServices.addView(fanfouRow, new TableLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

	}

	class FetchUserTask extends AsyncTask<String, Void, AuthList> {

		private String profile_image_path = null;

		@Override
		protected AuthList doInBackground(String... urls) {
			try {
				URL url = new URL(urls[0]);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setReadTimeout(5000);
				connection.setRequestMethod("GET");
				if (cookie != null) {
					connection.setRequestProperty("Cookie", cookie);
				}
				connection.connect();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				Gson gson = new Gson();
				AuthList authList = gson.fromJson(reader, AuthList.class);
				reader.close();
				if (authList.profile_image_url != null) {
					profile_image_path = downloadProfileImage(
							authList.profile_image_url, authList.userId);
				}
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

		private String downloadProfileImage(String image_url, String user_id) {
			String root = ProfileFragment.this.getActivity()
					.getExternalFilesDir(null).toString();
			File avatarDir = new File(root + "/avatar/" + user_id);
			String image_name = image_url
					.substring(image_url.lastIndexOf('/') + 1);
			if (image_name.indexOf('?') != -1) {
				image_name = image_name.substring(0,
						image_name.lastIndexOf('?'));
			}
			File avatar = new File(avatarDir.toString() + '/' + image_name);
			if (avatar.exists()) {
				return avatar.getAbsolutePath();
			}
			if (!avatarDir.exists()) {
				avatarDir.mkdirs();
			}

			try {
				URL url = new URL(image_url);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setReadTimeout(50000);
				connection.connect();
				InputStream in = connection.getInputStream();
				FileOutputStream fos = new FileOutputStream(avatar.getPath());
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				byte[] buffer = new byte[1024];
				try {
					while (in.read(buffer) > 0) {
						bos.write(buffer);
					}
					bos.flush();
				} finally {
					in.close();
					fos.getFD().sync();
					bos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return "";
			}
			return avatar.getAbsolutePath();
		}

		@Override
		protected void onPostExecute(AuthList authList) {
			ProfileFragment.this.authList = authList;
			homeButton.setText(authList.name + "����ҳ");
			homeButton.setVisibility(View.VISIBLE);
			homeButton.setTag(authList.userId);
			homeButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String userId = (String) v.getTag();
					Intent listIntent = new Intent(getActivity(), DocListActivity.class);
					listIntent.putExtra(DocListActivity.LIST_TYPE, userId);
					startActivity(listIntent);
				}	
			});
			profileName.setText(authList.name);
			if (profile_image_path != null) {
				BitmapDrawable avatar = new BitmapDrawable(
						ProfileFragment.this.getResources(), profile_image_path);
				profileName.setCompoundDrawablesWithIntrinsicBounds(avatar,
						null, null, null);
			}
			if (authList.description != null) {
				profileDesc.setText(authList.description);
			} else {
				profileDesc.setVisibility(View.GONE);
			}

			for (Auth auth : authList.auth_list) {
				TableRow row = (TableRow) ProfileFragment.this.getActivity()
						.getLayoutInflater()
						.inflate(R.layout.auth_service_table_row, null);
				TextView authServiceName = (TextView) row
						.findViewById(R.id.auth_service_name);
				authServiceName.setText(auth.name);
				TextView authStatus = (TextView) row
						.findViewById(R.id.auth_status);
				String authUrl = "";
				if (!auth.isInstalled) {
					if (authList.is_self) {
						StringBuffer buffer = new StringBuffer();
						authUrl = auth.service_id;
						buffer.append("<a href=\"");
						buffer.append(auth.auth_url);
						buffer.append("\">��¼</a>");
						authStatus.setText(Html.fromHtml(buffer.toString()));
					}
				} else {
					authUrl = auth.service_profile_url;
					StringBuffer buffer = new StringBuffer();
					buffer.append("<a href=\"");
					buffer.append(authUrl);
					buffer.append("\">");
					buffer.append(auth.service_username);
					buffer.append("</a>");
					authStatus.setText(Html.fromHtml(buffer.toString()));
				}
				row.setTag(authUrl);
				row.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						String authUrl = (String) v.getTag();
						if (!authUrl.startsWith("http://")) {
							Intent authIntent = new Intent(getActivity(),
									AuthActivity.class);
							authIntent.putExtra(AuthFragment.AUTH_SERVICE,
									authUrl);
							startActivity(authIntent);
						} else {
							Intent browserIntent = new Intent(
									Intent.ACTION_VIEW);
							browserIntent.setData(Uri.parse(authUrl));
							ProfileFragment.this.startActivity(browserIntent);
						}

					}
				});
				authServices.addView(row, new TableLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.logout) {
			new LogOutTask().execute();
		}	
	}
	
	class LogOutTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			if (cookie == null) {
				return null;
			}
			try {
				URL url = new URL(LOGOUT_URL);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setReadTimeout(5000);
				connection.setRequestMethod("GET");
					connection.setRequestProperty("Cookie", cookie);
				connection.connect();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
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
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(getActivity().getApplicationContext());
			prefs.edit().remove("cookie").commit();
			Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
			profileIntent.putExtra(ProfileActivity.USERID, authList.userId);
			startActivity(profileIntent);
		}
	}
}
