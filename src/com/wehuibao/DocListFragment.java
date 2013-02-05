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
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.google.gson.Gson;
import com.wehuibao.json.Doc;
import com.wehuibao.json.DocList;

public class DocListFragment extends SherlockListFragment {

	private static final String HOT_URL = "http://wehuibao.com/api/hot/";
	private List<Doc> docs = null;
	private DocAdapter adapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (docs == null) {
			docs = new ArrayList<Doc>();
			new DocFetchTask().execute(HOT_URL);
			adapter = new DocAdapter();
		}
		this.setListAdapter(adapter);
	}

	class DocAdapter extends ArrayAdapter<Doc> {

		public DocAdapter() {
			super(DocListFragment.this.getActivity(), R.layout.doc_row, R.id.doc_title, docs);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = super.getView(position, convertView, parent);
			Doc doc = docs.get(position);
			TextView title = (TextView) row.findViewById(R.id.doc_title);
			title.setText(doc.title);
			TextView abbrev = (TextView) row.findViewById(R.id.doc_abbrev);
			abbrev.setText(doc.abbrev_text);
			ImageView thumb = (ImageView) row.findViewById(R.id.doc_thumb);
			if (doc.thumb != null && doc.thumb.image_path != null) {
				Bitmap bm = BitmapFactory.decodeFile(doc.thumb.image_path);
				thumb.setImageBitmap(bm);
			} else {
				thumb.setVisibility(View.GONE);
			}
			return row;
		}
		
//		@Override
//		public int getCount() {
//			return docs.size();
//		}
	}

	class DocFetchTask extends AsyncTask<String, Doc, Void> {

		@Override
		protected Void doInBackground(String... urls) {
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
				DocList docList = gson.fromJson(reader, DocList.class);
				for (Doc doc : docList.items) {
					this.publishProgress(doc);
				}
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
		protected void onProgressUpdate(Doc... docs) {
			for (Doc doc : docs) {
				if (doc.thumb != null && doc.thumb.image_src != null) {
					doc.thumb.image_path = downloadDocThumbnail(
							doc.thumb.image_src, doc.docId);
				}
				adapter.add(doc);
			}
		}

		private String downloadDocThumbnail(String image_url, String doc_id) {
			String root = DocListFragment.this.getActivity()
					.getExternalFilesDir(null).toString();
			File avatarDir = new File(root + "/docs/" + doc_id);
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
					fos.getFD().sync();
					bos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return "";
			}
			return avatar.getAbsolutePath();
		}
	}
}
