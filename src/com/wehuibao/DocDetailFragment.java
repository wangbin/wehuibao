package com.wehuibao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.gson.Gson;
import com.wehuibao.json.Doc;
import com.wehuibao.json.User;

public class DocDetailFragment extends SherlockFragment {
	private TextView docTitle;
	private WebView docContent;
	private TableLayout sharerTable;

	private final static String DOC_URL = "http://wehuibao.com/api/doc/";
	private Doc doc = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.doc_detail, container, false);
		docTitle = (TextView) view.findViewById(R.id.doc_title);
		docContent = (WebView) view.findViewById(R.id.doc_content);
		sharerTable = (TableLayout) view.findViewById(R.id.sharerTable);
		Intent intent = this.getActivity().getIntent();
		String docId = intent.getStringExtra(DocDetailActivity.DOC_ID);
		if (doc == null || doc.docId != docId) {
			new FetchDocTask().execute(DOC_URL + docId);
		}
		return view;

	}

	class FetchDocTask extends AsyncTask<String, Void, Doc> {

		@Override
		protected Doc doInBackground(String... urls) {
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
				Doc doc = gson.fromJson(reader, Doc.class);
				return doc;
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
		protected void onPostExecute(Doc doc) {
			DocDetailFragment.this.doc = doc;
			docTitle.setText(doc.title);
			docContent.setVerticalFadingEdgeEnabled(false);
			// docContent.getSettings().setDefaultTextEncodingName("UTF-8") ;
			docContent.getSettings().setLayoutAlgorithm(
					LayoutAlgorithm.SINGLE_COLUMN);
			docContent.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
			docContent
					.loadData(doc.abbrev, "text/html; charset=utf-8", "UTF-8");
			if (doc.sharers.size() > 0) {
				for (User user : doc.sharers) {
					TableRow row = (TableRow) DocDetailFragment.this.getActivity().getLayoutInflater().inflate(R.layout.sharer, null);
					TextView name = (TextView) row.findViewById(R.id.sharer_name);
					name.setText(user.name);
					row.setTag(user.userId);
					row.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							String userId = (String) v.getTag();
							Toast.makeText(getActivity(), userId,
									Toast.LENGTH_LONG).show();
						}
					});
					sharerTable.addView(row, new TableLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT));
				}
			}
		}
	}

}
