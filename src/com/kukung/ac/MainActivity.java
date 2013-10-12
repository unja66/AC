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
        ((TextView)findViewById(R.id.producers)).setText("����: �ڸ���  PD:�ڽ�ȣ  ��ȸ: ������\n" +
        		"�г�: ���Դ�, ������, �����\n" +
        		"������: ������, �ȼ��� ��������:SI���� ������\n" +
        		"�뷡: �ֿ�ȣ, �躽, ������ �Ƴ��: ������\n���� ����: �ֿ��");
        
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
