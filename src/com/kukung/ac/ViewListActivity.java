package com.kukung.ac;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ViewListActivity extends Activity implements OnItemClickListener {
	private ListView mp3ListView;
	Mp3ListAdapter listItemAdapter;
	ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.list);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		if (PlayMp3Activity.mediaPlayer != null) {
			if (PlayMp3Activity.mediaPlayer.isPlaying()) {
				startPlayActivity(PlayMp3Activity.url, PlayMp3Activity.title, PlayMp3Activity.url);
				return;
			}
		}
		
		progressDialog = ProgressDialog.show(this, "", "방송 목록을 받아오는 중 입니다. 잠시 기다려주세요.", false);
		
		ArrayList<Mp3Item> mp3ItemList = new ArrayList<Mp3Item>();

		listItemAdapter = new Mp3ListAdapter(this, R.layout.mp3_item, mp3ItemList);
		mp3ListView = (ListView) this.findViewById(R.id.mp3List);
		mp3ListView.setAdapter(listItemAdapter);
		mp3ListView.setOnItemClickListener(this);
		
		(new LoadMp3ListFromNetwork()).execute();
		
	}
	
	private class LoadMp3ListFromNetwork extends
			AsyncTask<Void, Void, ArrayList<Mp3Item>> {
		@Override
		protected ArrayList<Mp3Item> doInBackground(Void... params) {
			ArrayList<Mp3Item> mp3ItemList = buildMp3ItemList();

			return mp3ItemList;
		}

		protected void onPostExecute(ArrayList<Mp3Item> result) {
			listItemAdapter = new Mp3ListAdapter(ViewListActivity.this, R.layout.mp3_item, result);
			mp3ListView.setAdapter(listItemAdapter);
			
			progressDialog.dismiss();
		}
	};

	private ArrayList<Mp3Item> buildMp3ItemList() {
		BufferedReader in = null;
		ArrayList<Mp3Item> mp3ItemList = new ArrayList<Mp3Item>();
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI("http://unja66.woobi.co.kr/ac/toc.txt"));
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				StringTokenizer stringTokenizer = new StringTokenizer(line, "|");
				mp3ItemList.add(new Mp3Item(stringTokenizer.nextToken(),
						stringTokenizer.nextToken(), stringTokenizer
								.nextToken()));
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return mp3ItemList;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		Mp3Item selectedItem = (Mp3Item) listItemAdapter.getItem(position);
		String url = selectedItem.getUrl();
		String title = selectedItem.getTitle();
		String fileName = selectedItem.getFileName();
		
		startPlayActivity(url, title, fileName);
	}

	private void startPlayActivity(String url, String title, String fileName) {
		Intent startPlayView = new Intent(this, PlayMp3Activity.class);
		startPlayView.putExtra("url", url);
		startPlayView.putExtra("title", title);
		startPlayView.putExtra("fileName", fileName);
		startActivity(startPlayView);
	}
}
