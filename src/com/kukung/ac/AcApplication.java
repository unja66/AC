package com.kukung.ac;

import android.app.Application;
import android.util.Log;

public class AcApplication extends Application{
	static Mp3Item selectedMp3Item;
	
	@Override
	public void onCreate() {
		Log.d("ac100", "application was invoked.");
	}

}
