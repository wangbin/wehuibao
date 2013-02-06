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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.gson.Gson;
import com.wehuibao.json.Doc;

public class DocDetailFragment extends SherlockFragment {
	private TextView docTitle;
	private WebView docContent;
	private TableLayout sharers;
	
	private final static String DOC_URL = "http://wehuibao.com/api/doc/";
	private Doc doc = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.doc_detail, container, false);
		docTitle = (TextView) view.findViewById(R.id.doc_title);
		docContent = (WebView) view.findViewById(R.id.doc_content);
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
			//docContent.getSettings().setDefaultTextEncodingName("UTF-8") ;
			docContent.loadData(doc.abbrev, "text/html; charset=utf-8", "UTF-8");
			Log.d("abbrev", doc.abbrev);
			Log.d("default encoding: ", docContent.getSettings().getDefaultTextEncodingName());
		}
	}

}
