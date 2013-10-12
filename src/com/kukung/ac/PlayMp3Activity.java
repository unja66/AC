package com.kukung.ac;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PlayMp3Activity extends Activity implements android.view.View.OnClickListener{
	private final static String sdCardLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ac100/";
	private static STATUS currentStatus = STATUS.BEFORE_DOWNLOAD;
	private static File mp3File;
	
	public static String url;
	public static String title;
	public static String fileName;
	
	private Button actionButton;

	private AsyncTask<Void, Integer, Boolean> downloadTask;
	private ProgressDialog dialog;
	
	public static MediaPlayer mediaPlayer;
	
	private enum STATUS {
		BEFORE_DOWNLOAD,
		DOWNLOADING,
		BEFORE_PLAY,
		PLAYING, PAUSED
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.play);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		Intent intent = getIntent();
		url = intent.getStringExtra("url");
		title = intent.getStringExtra("title");
		fileName = intent.getStringExtra("fileName");
		
		if (isEmpty(url) ||isEmpty(title) || isEmpty(fileName)) {
			showAlertAndGoToListView();
		}
		Log.d("ac", "title, url: "+title + ","+url);
		((TextView)findViewById(R.id.mp3Title)).setText(title);
		
		((Button)findViewById(R.id.backToIndexButton)).setOnClickListener(this);
		
		actionButton = (Button)findViewById(R.id.actionButton);
		actionButton.setOnClickListener(this);
		
		Log.d("ac", "1 ====");
		if (currentStatus == STATUS.PLAYING) {
			initButtonBaseOnCurrentStatus();
			return;
		}
		
		Log.d("ac", "2 ====");
		if (isThereMp3File(fileName)) {
			currentStatus = STATUS.BEFORE_PLAY;
			Log.d("ac", "3 ====");
		} else {
			currentStatus = STATUS.BEFORE_DOWNLOAD;
			Log.d("ac", "4 ====");
		}
		
		Log.d("ac", "5 ====");
		initButtonBaseOnCurrentStatus();
		
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
		
	}
	
	@Override
	public void onClick(View v) {
		int resourceId = v.getId();
		switch (resourceId) {
		case R.id.backToIndexButton:
			stopPlayingAndGoToIndexActivity();
			break;
		case R.id.actionButton:
			updateStatusAndInitNextStep();
			break;
		default:
			Toast.makeText(this, "예상 외 상황~ @,.@", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	private void updateStatusAndInitNextStep() {
		switch (currentStatus) {
		case BEFORE_DOWNLOAD:
			startDownload();
			break;
		case DOWNLOADING:
			Toast.makeText(this, "이거 클릭되면 안되는데~ ㅠ,.ㅠ", Toast.LENGTH_SHORT).show();
			break;
		case BEFORE_PLAY:
			startPlay();
			break;
		case PLAYING:
			pausePlay();
			break;
		case PAUSED:
			startPlay();
			break;
		}
	}

	private void pausePlay() {
		currentStatus = STATUS.PAUSED;
		initButtonBaseOnCurrentStatus();
		try {
			mediaPlayer.pause();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startPlay() {
		if (currentStatus == STATUS.BEFORE_PLAY)  {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.stop();
					currentStatus = STATUS.BEFORE_PLAY;
					initButtonBaseOnCurrentStatus();
				}
			});
		} 
		try {
			
			if (currentStatus == STATUS.BEFORE_PLAY) {
//				mediaPlayer.stop();
				mediaPlayer.setDataSource(mp3File.getAbsolutePath());
				mediaPlayer.prepare();
			}
//			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		currentStatus = STATUS.PLAYING;
		initButtonBaseOnCurrentStatus();
		
	}

	
	private void startDownload() {
		currentStatus = STATUS.DOWNLOADING;
		initButtonBaseOnCurrentStatus();
		
		dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Downloading...");
        // set the progress to be horizontal
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // reset the bar to the default value of 0
        dialog.setProgress(0);
        dialog.setMax(100);
        
        dialog.show();
        
		downloadTask = new DownloadTask();
		downloadTask.execute();
	}
	
	private class DownloadTask extends AsyncTask<Void, Integer, Boolean> {
		@Override
		protected void onPreExecute(){
			if (mp3File.exists()) {
				mp3File.delete();
			} else {
				mp3File.getParentFile().mkdirs();
			}
			
			publishProgress(0);
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			Boolean fileDownloaded = new Boolean(false);
			
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			
			try{
				HttpResponse response = client.execute(request);
				StatusLine statusLine = response.getStatusLine();
				
				if (statusLine.getStatusCode() == 200) {
					HttpEntity entity = response.getEntity();
					InputStream inputStream = entity.getContent();
					int fileSize = (int) entity.getContentLength();
					FileOutputStream out = new FileOutputStream(mp3File);
					
					byte buf[] = new byte[8192];
					int len;
					int totalRead = 0;
					
					while ((len = inputStream.read(buf)) > 0) {
						totalRead += len;
						publishProgress((totalRead * 100) / fileSize);
						out.write(buf, 0, len);
					}
					out.close();
				} else {
					throw new IOException("다운 중 에러: "+statusLine);
				}
			} catch (Exception e) {
				Log.e("ac", "다운로드 중 실패: " + e.getMessage(), e);
				return Boolean.valueOf(false);
			}
			return Boolean.valueOf(true);
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress) {
			setProgressPercent(progress[0]);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			dialog.dismiss();
			if (result.booleanValue()) {
				currentStatus = STATUS.BEFORE_PLAY;
				initButtonBaseOnCurrentStatus();
			} else {
				Toast.makeText(PlayMp3Activity.this, "나중에 재시도 부탁 드립니다.", Toast.LENGTH_SHORT).show();
				if (mp3File.exists()) {
					mp3File.delete();
				}
				currentStatus = STATUS.BEFORE_DOWNLOAD;
				initButtonBaseOnCurrentStatus();
			}
		}
	};
	
	private void setProgressPercent(Integer progressRate) {
		dialog.setProgress(progressRate.intValue());
	}
	
	private void completeDownload() {
		currentStatus = STATUS.BEFORE_PLAY;
		initButtonBaseOnCurrentStatus();
	}

	private void stopPlayingAndGoToIndexActivity() {
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
			mediaPlayer.release();
			mediaPlayer = null;
		}
		
		currentStatus = STATUS.BEFORE_DOWNLOAD;
		
		Intent intent = new Intent(this, ViewListActivity.class);
		startActivity(intent);
		finish();
	}

	private void initButtonBaseOnCurrentStatus() {
		switch (currentStatus) {
		case BEFORE_DOWNLOAD:
			actionButton.setText("파일 다운 받기");
			actionButton.setEnabled(true);
			break;
		case DOWNLOADING:
			actionButton.setText("100% 다운 받은 후 들을 수 있습니다.");
			actionButton.setEnabled(false);
			break;
		case BEFORE_PLAY:
			actionButton.setText("방송 듣기");
			actionButton.setEnabled(true);
			break;
		case PLAYING:
			actionButton.setText("일시 정지 하기");
			actionButton.setEnabled(true);
			break;
		case PAUSED:
			actionButton.setText("이어서 듣기");
			actionButton.setEnabled(true);
		default:
			break;
		}
	}

	private boolean isThereMp3File(String fileName) {
		String filePath = sdCardLocation + fileName;
		mp3File = new File(filePath);
		if (mp3File.exists()) {
			currentStatus = STATUS.BEFORE_PLAY;
			return true;
		} else {
			currentStatus = STATUS.BEFORE_DOWNLOAD;
			return false;
		}
	}

	private void showAlertAndGoToListView() {
		AlertDialog dialog = new AlertDialog.Builder(this)
			.setTitle("알림")
			.setMessage("듣고자 하는 목록을 선택하시기 바랍니다. 목록 화면으로 돌아갑니다.")
			.setPositiveButton("확인", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					Intent intent = new Intent(PlayMp3Activity.this, ViewListActivity.class);
					startActivity(intent);
					finish();
				}
			})
			.show();
	}

	private boolean isEmpty(String url) {
		if (url == null) return true;
		if (url.trim().equals("")) return true;
		return false;
	}

	
}
