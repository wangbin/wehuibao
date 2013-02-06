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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;
import com.wehuibao.json.Doc;
import com.wehuibao.json.DocList;

public class DocListFragment extends SherlockListFragment implements OnClickListener {

	private static final String HOT_URL = "http://wehuibao.com/api/hot/";
	private List<Doc> docs = null;
	private DocAdapter adapter;
	private String start = null;
	private TextView loadMore;
	private ProgressBar loadMorePB;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
	    setHasOptionsMenu(true);
	    
		if (docs == null) {
			docs = new ArrayList<Doc>();
			new DocFetchTask().execute(HOT_URL);
			adapter = new DocAdapter();
		}
		View footer = this.getActivity().getLayoutInflater().inflate(
				R.layout.load_more, null);
		this.getListView().addFooterView(footer);
		loadMore = (TextView) this.getActivity().findViewById(R.id.load_more);
		loadMorePB = (ProgressBar) this.getActivity().findViewById(R.id.load_more_pb);
		loadMore.setOnClickListener(this);
		this.setListAdapter(adapter);
	}
	
	@Override
	  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.doc_list, menu);
	    super.onCreateOptionsMenu(menu, inflater);
	  }
	
	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_refresh) {
			adapter.clear();
			start = null;
			new DocFetchTask().execute(HOT_URL);
			
		}
		return super.onOptionsItemSelected(item);
		
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
	}

	class DocFetchTask extends AsyncTask<String, Doc, Void> {

		@Override
		protected Void doInBackground(String... urls) {
			try {
				String urlStr = urls[0];
				//FIXME: this not working
				if (start != null) {
					Log.d("start is: ", start);
					urlStr += "?start=" + start + "&count=-20";
				}
				URL url = new URL(urlStr);
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
					if (doc.thumb != null && doc.thumb.image_src != null) {
						doc.thumb.image_path = downloadDocThumbnail(
								doc.thumb.image_src, doc.docId);
					}
					start = doc.docId;
					Log.d("doc.id: ", doc.docId);
					Log.d("doc.title", doc.title);
					this.publishProgress(doc);
				}
				Log.d("start: ", start);
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
				adapter.add(doc);
			}
		}
		
		@Override
		protected void onPostExecute(Void unused) {
			if (loadMorePB.getVisibility() == View.VISIBLE) {
				loadMorePB.setVisibility(View.GONE);
				loadMore.setVisibility(View.VISIBLE);
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

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.load_more) {
			v.setVisibility(View.GONE);
			loadMorePB.setVisibility(View.VISIBLE);
			new DocFetchTask().execute(HOT_URL);
		}
	}
}
