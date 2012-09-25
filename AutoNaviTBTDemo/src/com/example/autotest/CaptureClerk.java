package com.example.autotest;

import android.graphics.Bitmap;
import android.util.Log;

public class CaptureClerk {
    // -1 
    private int product = -1; 
 
    private Bitmap bmp = null;
    private static String LOG_TAG = "tbt";
    private final static boolean LOGD_ENABLED = Utils.LOGD_ENABLED;
    
    public synchronized void setProduct(int product) { 
        while(this.product != -1) { 
            try {
                wait(); 
            }   
            catch(InterruptedException e) { 
                e.printStackTrace(); 
            }   
        }   
 
        this.product = product; 
        if (LOGD_ENABLED) Log.d(LOG_TAG, "producer set " + this.product); 

        notify(); 
    }
    

    
    // called by consumer
    public synchronized int getProduct() { 
        while(this.product == -1) { 
            try {
                // No product
                wait(); 
            }   
            catch(InterruptedException e) { 
                e.printStackTrace(); 
            }   
        }
        
        int p = this.product;
        if (LOGD_ENABLED) Log.d(LOG_TAG, "consumer got " + this.product);
        this.product = -1;

        // notify producer go on
        notify();

        return p;
    }
    
    public synchronized void setCaptureProduct(Bitmap product) { 
        while(this.bmp != null) { 
            try {
                wait(); 
            }   
            catch(InterruptedException e) { 
                e.printStackTrace(); 
            }   
        }   
 
        this.bmp = product; 
        if (LOGD_ENABLED) Log.d(LOG_TAG, "producer set " + this.bmp.hashCode()); 

        notify(); 
    } 
    
    public synchronized Bitmap getCaptureProduct() { 
        while(this.bmp == null) { 
            try {
                // No product
                wait(); 
            }   
            catch(InterruptedException e) { 
                e.printStackTrace(); 
            }   
        }
        
        Bitmap p = this.bmp;
        if (LOGD_ENABLED) Log.d(LOG_TAG, "consumer got " + this.bmp.hashCode());
        this.bmp = null;

        // notify producer go on
        notify();

        return p;
    }
}

