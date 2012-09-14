package com.example.autotest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.util.Log;
import android.view.View;

public class Utils {
    private static Bitmap mCapture;
    
    public static void initBitmap(int thumbnailWidth, int thumbnailHeight){
        mCapture = Bitmap.createBitmap(thumbnailWidth, thumbnailHeight,
                Bitmap.Config.RGB_565);
    }
    
    public static Bitmap createScreenshot1(View view, int thumbnailWidth, int thumbnailHeight) {
        if (view != null) {
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            mCapture = Bitmap.createBitmap(view.getDrawingCache());
            view.destroyDrawingCache();
            view.setDrawingCacheEnabled(false);
            return mCapture;
        }
        return null;
    }
    
    public static Bitmap createScreenshot2(View view) {
        if (view != null) {  
            Canvas c = new Canvas(mCapture);
            view.draw(c);
            return mCapture;
        }
        return null;
    }
    
    public static Bitmap createScreenshot3(View view, int thumbnailWidth, int thumbnailHeight) {
        if (view != null) {
            Bitmap mCapture;
            try {
                mCapture = Bitmap.createBitmap(thumbnailWidth, thumbnailHeight,
                        Bitmap.Config.RGB_565);
            } catch (OutOfMemoryError e) {
                return null;
            }   
            Canvas c = new Canvas(mCapture);
            final int left = view.getScrollX();
            final int top = view.getScrollY();
            c.translate(-left, -top);
            //c.scale(0.65f, 0.65f, left, top);
            try {
                // draw webview may nullpoint
                view.draw(c);
            } catch (Exception e) {
            }
            return mCapture;
        }
        return null;
    }

    public static boolean saveScreenshot(Activity activity, String fileName, 
            Bitmap screenshot, boolean sdcard) {
        try {
            FileOutputStream fos = null;
            if (!sdcard) {
                fos = activity.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
            } else {
                File f = new File(fileName);
                f.createNewFile();     
                fos = new FileOutputStream(f);
            }    
            screenshot.compress(Bitmap.CompressFormat.JPEG, 70, fos);
            fos.flush();
            fos.close();
            return true;
        } catch (IOException e) { 
            e.printStackTrace();
        }    
        return false;
    }
    
    /**
     * Transform a int to be low byte in beginning and high byte in end.
     * 
     */
    public static int toLHInt(int in) {
        int out = 0;
        out = (in & 0xff) << 24;
        out |= (in & 0xff00) << 8;
        out |= (in & 0xff0000) >> 8;
        out |= (in & 0xff000000) >> 24;
        return out;
    }
    
    public static byte[] toLHByte(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
      }
}
