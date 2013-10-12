package com.kukung.ac;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.activity_main);
        ((TextView)findViewById(R.id.producers)).setText("제작: 박명하  PD:박승호  사회: 정혜원\n" +
        		"패널: 오규덕, 이윤즈, 정재봉\n" +
        		"디자인: 노현진, 안수지 뮤직디렉터:SI뮤직 윤영식\n" +
        		"노래: 최용호, 김봄, 김재희 아나운서: 정석원\n어플 제작: 최운용");
        
        Thread showingSplashThread = new Thread() {
        	public void run() {
        		super.run();
        		
        		try {
					sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					Intent intent = new Intent(MainActivity.this, ViewListActivity.class);
					startActivity(intent);
					finish();
				}
        	}
        };
        showingSplashThread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
