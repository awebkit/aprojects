package com.autonavi.tbt.demo;

import java.util.Timer;
import java.util.TimerTask;

import com.example.autotest.BackgroundHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class AutoNaviTBTDemoActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting);
        
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
         @Override
	         public void run() {
	        	 Intent intent = new Intent(AutoNaviTBTDemoActivity.this, TBTNaviWarningActivity.class); 
	        	 startActivity(intent);
	        	 AutoNaviTBTDemoActivity.this.finish();
	         }
         
        };
    
        timer.schedule(task, 2000);
        BackgroundHandler.init(this);
    }
}