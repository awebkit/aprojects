package com.autonavi.tbt.demo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.text.format.Time;


public class CustemException implements UncaughtExceptionHandler {

	private Thread.UncaughtExceptionHandler defaultExceptionHandler;
	
	private static CustemException custemException;
	
	private CustemException(){
		// nothing to do
	}
	
	public static CustemException getInstance(){
		if (custemException == null) {
			custemException = new CustemException();
		}
		
		return custemException;
	}
	
	public void init(Context context) {
		defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	
	@Override
	public void uncaughtException(Thread arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		HandleException(arg1);
		
		defaultExceptionHandler.uncaughtException(arg0, arg1);
	}
	
	private void HandleException(Throwable ex) {  
		StringBuffer exceptionStr = new StringBuffer();  
		exceptionStr.append(ex.getMessage());  

		StackTraceElement[] elements = ex.getStackTrace();  
		for (int i = 0; i < elements.length; i++) {  
			exceptionStr.append("\n");
			exceptionStr.append(elements[i].toString()); 			
		} 
				
		// save log		
		try {
			Time mTime = new Time();
			mTime.setToNow();
			
			FileWriter fw = new FileWriter("/mnt/sdcard/TBT_CrashLog_" + mTime.year + (mTime.month+1) + 
									mTime.monthDay + mTime.hour + mTime.minute + mTime.second + ".txt");
			 // 将缓冲对文件的输出
	        BufferedWriter bw = new BufferedWriter(fw); 
	        bw.write(exceptionStr.toString());
	        bw.newLine();
	        
	        bw.flush();
	        bw.close();
	        fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}       
	}

}
