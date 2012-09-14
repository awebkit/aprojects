package com.example.autotest;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.autonavi.tbt.demo.TBTNaviDemoMapView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

public class CaptureServer implements Runnable {

    private static final String LOG_TAG = "tbt";
    private static int CAPTURE_PORT = 54321;
    
    private static Activity mActivity;
    private int mWidth;
    private int mHeight;
    private static Bitmap mCapture;
    private static boolean mInsideUI;
    
    
    private static CaptureClerk mCaptureClerk;
    
    private static int pos = 10;
    
    CaptureServer(Activity activity) {
        mActivity = activity;
        
        mWidth = mActivity.getWindowManager().getDefaultDisplay().getWidth();
        mHeight = mActivity.getWindowManager().getDefaultDisplay().getHeight();
        Utils.initBitmap(mWidth, mHeight);
        mCapture = Bitmap.createBitmap(mWidth, mHeight,
                Bitmap.Config.RGB_565);

        mCaptureClerk = null;
    }

    public static void setClerk(CaptureClerk clerk){
        mCaptureClerk = clerk;
    }
    
    public void setActivity(Activity activity){
        setActivity(activity, false);
    }
    
    public void setActivity(Activity activity, boolean useActivityMethod){
        mActivity = activity;
        mInsideUI = useActivityMethod;
        Log.i(LOG_TAG, "set activity " + mActivity.hashCode());
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        Log.i(LOG_TAG, "Create capture thread begin1122222");

        while (true)
        {
            try
            {
                ServerSocket serverSocket = new ServerSocket(CAPTURE_PORT);
                Socket client = serverSocket.accept();
                Log.i(LOG_TAG, "accept ");
                DataOutputStream os = new DataOutputStream(client.getOutputStream());
                while (true) {
                    //sleep 5s
                    SystemClock.sleep(200);
                    if (client == null)
                        break;

                    byte[] buffer = prepareCaptureMessage();
                    if (buffer != null) {
                        os.write(buffer);
                        os.flush();
                    }
                }
                os.close();

            } catch (Exception e)
            {
                Log.i(LOG_TAG, "got exception begin?!!!");
                e.printStackTrace();
                Log.i(LOG_TAG, "got exception end ?!");
            }
        }
    }
    
    private byte[] prepareCaptureMessage() {
        Log.i(LOG_TAG, "prepareCaptureMessage activity " + mActivity.hashCode());

        if (mInsideUI) {
            ((TBTNaviDemoMapView) mActivity).createTestMessage();
            
            Bitmap bmp = mCaptureClerk.getCaptureProduct();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, os);
            byte[] byteArray = os.toByteArray();

//            pos++;
//            Utils.saveScreenshot(mActivity, "/sdcard/jieping" + pos + ".jpg", bmp, true);

            return new MessageBody(mWidth, mHeight, byteArray.length, byteArray).getBuf();
        }
        
        View v = mActivity.getWindow().getDecorView();
        if (v == null)
            return null;

        // Bitmap bmp = Utils.createScreenshot2(v);
        Canvas c = new Canvas(mCapture);
        v.draw(c);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        mCapture.compress(Bitmap.CompressFormat.JPEG, 50, os);

        // TEST
//        pos++;
//        Utils.saveScreenshot(mActivity, "/sdcard/jieping" + pos + ".jpg", mCapture, true);

        byte[] byteArray = os.toByteArray();
        Log.i(LOG_TAG, "prepareCaptureMessage end 222");
        return new MessageBody(mWidth, mHeight, byteArray.length, byteArray).getBuf();
    }

}
