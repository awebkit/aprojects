package com.autonavi.tbt.navi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.os.Environment;
import android.os.SystemClock;

import com.autonavi.tbt.TBT;
import com.autonavi.tbt.demo.TBTNaviDemoMapView;

public class SetGPSInfor extends Thread{

	private TBT m_tbt;	
	private TBTNaviDemoMapView mTbtNavi;
	public SetGPSInfor(TBTNaviDemoMapView tbtNavi, TBT tbt){
		m_tbt = tbt;
		mTbtNavi = tbtNavi;	
	}	
	public void run(){
    	try{
    		FileInputStream fis=new FileInputStream(Environment.getExternalStorageDirectory().getPath() + "/GPSLog.txt");
    		 InputStreamReader isr=new InputStreamReader(fis);       
    		 BufferedReader br = new BufferedReader(isr);     
    		 String line="";       
    		 String[] arrs=null;
    		 while ((line=br.readLine())!=null) {  
    			 NmeaData nmea =new NmeaData();
    			 	//if (!line.contains("3A")) 
    			 { // valid GPS log
	    			    arrs=line.split(",");       
	    		  
	    				nmea.m_Longitude = Double.parseDouble(arrs[0]);		// 纬度, 单位度 (正值为北纬, 负值为南纬)
	    	    		nmea.m_Latitude = Double.parseDouble(arrs[1]);		// 经度, 单位度 (正值为东经, 负值为西经)
	    	    		nmea.m_Altitude = 300;		// 海拔, 单位米
	    	    		nmea.m_Speed = Double.parseDouble(arrs[2]);			// 速度, 单位千米/时
	    	    		nmea.m_Track = Double.parseDouble(arrs[3]);			// 方向角, 单位度
	    	    		nmea.m_MagVariation = 45;	// 地磁变化, 单位度 
	    	    		nmea.m_Pdop = Double.parseDouble(arrs[4]);			// 位置精度参数
	    	    		nmea.m_Hdop = 1;			// 水平精度参数
	    	    		nmea.m_Vdop = 1;			// 垂直精度参数
	    	    		nmea.m_NumSats = 4;			// 星空图卫星个数
	    	    		nmea.m_FixedMode = 4;			// GPS定位质量
	    	    		nmea.m_BJYear = Integer.parseInt(arrs[5]);			// GPS(BJ)时间－－年
	    	    		nmea.m_BJMonth = Integer.parseInt(arrs[6]);			// GPS(BJ)时间－－月
	    	    		nmea.m_BJDay = Integer.parseInt(arrs[7]);			// GPS(BJ)时间－－日
	    	    		nmea.m_BJHour = Integer.parseInt(arrs[8]);			// GPS(BJ)时间－－时
	    	    		nmea.m_BJMinute = Integer.parseInt(arrs[9]);			// GPS(BJ)时间－－分
	    	    		nmea.m_BJSecond = Integer.parseInt(arrs[10]);			// GPS(BJ)时间－－秒
	    	    		nmea.m_ValidChar = 'A';		//定位成功与否的标志
	    	    		nmea.m_LastFixQuality = 1; 	// 前一次定位质量，0 无效坐标，1 有效坐标，2 DGPS更正坐标
	    	    		nmea.m_HasCoordEverBeenValid = true;	//是否曾经定位成功过
	    	    		//m_tbt.setGPSInfor(nmea);
	    	    		m_tbt.setGPSInfor(2, nmea.m_Longitude, nmea.m_Latitude, nmea.m_Speed, nmea.m_Track,
	    	    				nmea.m_BJYear, nmea.m_BJMonth, nmea.m_BJDay, nmea.m_BJHour, nmea.m_BJMinute, nmea.m_BJSecond);

						// update GPS view
						mTbtNavi.notifyGPSInfo(nmea);
	    			 }  	
    	    		
    	    		SystemClock.sleep(1000);    	    		
    	    		//sleep(1000);    			   		
    		}    	        
    	}
    	catch(Exception e){
    		e.printStackTrace();    		
    	} 
	}
}
