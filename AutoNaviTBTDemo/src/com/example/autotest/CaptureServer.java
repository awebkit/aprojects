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
    private static boolean mInsideUI;
    
    
    private static CaptureClerk mCaptureClerk;
    
    private static int pos = 10;
    
    CaptureServer(Activity activity) {
        mActivity = activity;
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

        View v = mActivity.getWindow().getDecorView();
        if (v == null)
            return null;

        int width = mActivity.getWindowManager().getDefaultDisplay().getWidth();
        int height = mActivity.getWindowManager().getDefaultDisplay().getHeight();

        if (mInsideUI) {
            ((TBTNaviDemoMapView) mActivity).createTestMessage();

            Bitmap bmp = mCaptureClerk.getCaptureProduct();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, os);
            byte[] byteArray = os.toByteArray();

//            pos++;
//            Utils.saveScreenshot(mActivity, "/sdcard/jieping" + pos + ".jpg", bmp, true);

            return new MessageBody(width, height, byteArray.length, byteArray).getBuf();
        }

         Bitmap bmp = Utils.createScreenshot3(v, width, height);


        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, os);

        // TEST
//        pos++;
//        Utils.saveScreenshot(mActivity, "/sdcard/jieping" + pos + ".jpg", bmp, true);

        byte[] byteArray = os.toByteArray();
        Log.i(LOG_TAG, "prepareCaptureMessage end 222");
        return new MessageBody(width, height, byteArray.length, byteArray).getBuf();
    }

}
