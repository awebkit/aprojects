package com.example.autotest;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

public class Hotspot implements Runnable {

    private static String LOG_TAG = "hotspot";
    private static final String SSID = "ANDROIDTEST";
    private Activity mActivity;
    
    Hotspot(Activity activity){
        mActivity = activity;
    }
    @Override
    public void run() {
        Log.i(LOG_TAG,"Create hotspot thread begin" );
        // disable wifi 
        WifiManager wifiManager = (WifiManager) mActivity.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
        
        try {  
            WifiConfiguration apConfig = new WifiConfiguration();
            
            apConfig.SSID = SSID;  
            // no password now 
            //apConfig.preSharedKey = PASSWORD;  
            // reflect  
            Method method = wifiManager.getClass().getMethod(  
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            
            method.invoke(wifiManager, apConfig, true);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        Log.i(LOG_TAG,"Create hotspot thread end" );
    }

}
