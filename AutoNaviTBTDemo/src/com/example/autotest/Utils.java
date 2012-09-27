package com.example.autotest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Log;
import android.view.View;

public class Utils {
    
    public final static boolean LOGD_ENABLED = false;
    public final static boolean CAPTURE_TEST = false;
//    private static Bitmap mCapture;
//    
//    public static void initBitmap(int thumbnailWidth, int thumbnailHeight){
//        mCapture = Bitmap.createBitmap(thumbnailWidth, thumbnailHeight,
//                Bitmap.Config.RGB_565);
//    }
//    
//    public static Bitmap createScreenshot1(View view, int thumbnailWidth, int thumbnailHeight) {
//        if (view != null) {
//            view.setDrawingCacheEnabled(true);
//            view.buildDrawingCache();
//            mCapture = Bitmap.createBitmap(view.getDrawingCache());
//            view.destroyDrawingCache();
//            view.setDrawingCacheEnabled(false);
//            return mCapture;
//        }
//        return null;
//    }
//    
//    public static Bitmap createScreenshot2(View view) {
//        if (view != null) {
//            Canvas c = new Canvas(mCapture);
//            //view.set
//            view.draw(c);
//            return mCapture;
//        }
//        return null;
//    }
    
    public static Bitmap createScreenshot3(View view, int thumbnailWidth, int thumbnailHeight, int top) {
        if (view != null) {
            Bitmap mCapture;
            try {
                mCapture = Bitmap.createBitmap(thumbnailWidth, thumbnailHeight,
                        Bitmap.Config.RGB_565);
            } catch (OutOfMemoryError e) {
                return null;
            }   
            Canvas c = new Canvas(mCapture);
//            final int left = view.getScrollX();
//            final int top = view.getScrollY();
            c.translate(0, -top);
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
    
    public static Bitmap createScreenshot4(View view, int thumbnailWidth, int thumbnailHeight) {
        if (view != null) {
            Bitmap mCapture;
            try {
                mCapture = Bitmap.createBitmap(thumbnailWidth, thumbnailHeight,
                        Bitmap.Config.RGB_565);
            } catch (OutOfMemoryError e) {
                return null;
            }   
            Canvas c = new Canvas(mCapture);
            Paint transPainter = new Paint();
            transPainter.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            c.drawRect(0, 0, mCapture.getWidth(), mCapture.getHeight(), transPainter);
//            final int left = view.getScrollX();
//            final int top = view.getScrollY();
//            c.translate(-left, -top);
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
    
    public static Bitmap createPngScreenshot(View view, int thumbnailWidth, int thumbnailHeight, int top) {
        if (view != null) {
            Bitmap mCapture;
            try {
                mCapture = Bitmap.createBitmap(thumbnailWidth, thumbnailHeight,
                        Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError e) {
                return null;
            }   
            Canvas c = new Canvas(mCapture);
//            final int left = view.getScrollX();
//            final int top = view.getScrollY();
            c.translate(0, -top);
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
    
    public static Bitmap createPngScreenshot2(View view, int thumbnailWidth, int thumbnailHeight, int top) {
        if (view != null) {
            Bitmap mCapture;
            try {
                mCapture = Bitmap.createBitmap(thumbnailWidth, thumbnailHeight,
                        Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError e) {
                return null;
            }   
            Canvas c = new Canvas(mCapture);
            c.drawColor(Color.BLACK);
//            final int left = view.getScrollX();
//            final int top = view.getScrollY();
            c.translate(0, -top);
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
    
    public static boolean savePngScreenshot(Activity activity, String fileName, 
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
            screenshot.compress(Bitmap.CompressFormat.PNG, 70, fos);
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
    
    public static int ByteArraytoInt(byte[] b) {
        int iOutcome = 0;
        byte bLoop;
        for (int i = 0; i < 4; i++) {
                bLoop = b[i];
                iOutcome += (bLoop & 0xff) << (8 * i);
        }
        return iOutcome;
     }

    public static short ByteArraytoShort(byte[] b) {
        short iOutcome = 0;
        byte bLoop;
        for (int i = 0; i < 2; i++) {
            bLoop = b[i];
            iOutcome += (bLoop & 0xff) << (8 * i);
        }
        return iOutcome;
    }
    
    /** 
     * Mix two Bitmap as one. 
     *  
     * @param bitmapOne 
     * @param bitmapTwo 
     * @param point 
     *            where the second bitmap is painted. 
     * @return 
     */  
    public static Bitmap mixtureBitmap(Bitmap first, Bitmap second, PointF fromPoint, float dimAmount) {  
        if (first == null || second == null || fromPoint == null) {  
            return null;  
        }  
        Bitmap newBitmap = Bitmap.createBitmap(first.getWidth(), first.getHeight(), Bitmap.Config.RGB_565);  
        Canvas cv = new Canvas(newBitmap); 
        Paint paint = new Paint();
        if (dimAmount != 0){
            int alpha = (int) (255 * dimAmount);
            paint.setAlpha(alpha);
        }
        cv.drawBitmap(first, 0, 0, paint);
        
        Paint paint2 = new Paint();
        paint2.setColor(Color.TRANSPARENT);
        
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);  
        cv.save(Canvas.ALL_SAVE_FLAG);  
        cv.restore();  
        return newBitmap;  
    }
    
    public static Bitmap mixtureBitmap2(Bitmap first, Bitmap second, PointF fromPoint, float dimAmount, float usealpha) {  
        if (first == null || second == null || fromPoint == null) {  
            return null;  
        }  
        Bitmap newBitmap = Bitmap.createBitmap(first.getWidth(), first.getHeight(), Bitmap.Config.RGB_565);  
        Canvas cv = new Canvas(newBitmap); 
        Paint paint = new Paint();
        if (dimAmount != 0){
            int alpha = (int) (255 * dimAmount);
            paint.setAlpha(alpha);
        }
        cv.drawBitmap(first, 0, 0, paint);
        
        Paint paint2 = new Paint();
        paint2.setColor(Color.WHITE);
        
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);  
        cv.save(Canvas.ALL_SAVE_FLAG);  
        cv.restore();  
        return newBitmap;  
    }
}
