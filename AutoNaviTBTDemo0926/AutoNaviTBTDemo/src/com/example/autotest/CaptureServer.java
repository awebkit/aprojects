package com.example.autotest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.autonavi.tbt.demo.TBTNaviDemoMapView;

import android.app.Activity;
import android.app.Instrumentation;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class CaptureServer implements Runnable {

    private final static boolean LOGD_ENABLED = Utils.LOGD_ENABLED;
    private final static boolean CAPTURE_TEST = Utils.CAPTURE_TEST;
    
    private static final String LOG_TAG = "tbt";
    private static int CAPTURE_PORT = 54321;
    
    private static Activity mActivity;
    private static boolean mInsideUI;
    
    
    private static CaptureClerk mCaptureClerk;
    
    private static int pos = 10;
    
    private Socket          client   = null;
    private ServerSocket    serverSocket   = null;
    private DataOutputStream streamOut =  null;
    
    CaptureServer(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void run() {
        if (LOGD_ENABLED) Log.d(LOG_TAG, "Creating capture thread ...");

        while (true) {
            try {
                serverSocket = new ServerSocket(CAPTURE_PORT);

                client = serverSocket.accept();
                if (LOGD_ENABLED) Log.d(LOG_TAG, "capture server accept socket");
                open();

                boolean done = false; // No meaning for done.
                while (!done && client != null) {
                    try {
                        if (true) {
                            if (socketClosed()) {
                                if (LOGD_ENABLED) Log.d(LOG_TAG, "======== capture client socket closed =======");
                                break;
                            }                          

                            byte[] buffer = prepareCaptureMessage();
                            if (buffer != null) {
                                streamOut.write(buffer);
                                streamOut.flush();
                            }
                            if (CAPTURE_TEST){
                                SystemClock.sleep(1000);
                            } else
                                SystemClock.sleep(100);
                        }
                    } catch (IOException e) {
                        if (LOGD_ENABLED) Log.d(LOG_TAG, "capture thread exception, close");
                        done = true;
                    }
                }
                close();
            } catch (IOException e) {
                if (LOGD_ENABLED) Log.d(LOG_TAG, "capture thread exception, exit");
                e.printStackTrace();
            }
        }
    }

    private boolean socketClosed() {
        try {
            client.sendUrgentData(0);
            return false;
        } catch (Exception se) {
            return true;
        }
    }

    private void close() throws IOException {
        if (LOGD_ENABLED) Log.d(LOG_TAG, "No data, close capture server socket");
        if (client != null)
            client.close();
        if (streamOut != null)
            streamOut.close(); 
        if (serverSocket != null)
            serverSocket.close();
    }

    private void open() throws IOException {
        // TODO Auto-generated method stub
        streamOut = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
    }
    
    private byte[] prepareCaptureMessage() {
        if (LOGD_ENABLED)
            Log.d(LOG_TAG, "prepareCaptureMessage activity " + mActivity.hashCode());

        View v = mActivity.getWindow().getDecorView();
        if (v == null)
            return null;

        int width = v.getWidth();
        int height = v.getHeight();
        if (width <=0 || height <=0)
            return null;
        Rect outRect = new Rect();
        v.getWindowVisibleDisplayFrame(outRect);

        height = height - outRect.top;
        if (LOGD_ENABLED)
            Log.d(LOG_TAG, "prepareCaptureMessage activity height:" + height);
        Bitmap bmp = ScreenShotHelper.getCapture();

        if (bmp == null)
            return null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, os);
        byte[] byteArray = os.toByteArray();

        if (CAPTURE_TEST) {
            pos++;
            Utils.saveScreenshot(mActivity, "/sdcard/jieping" + pos + ".jpg", bmp, true);
        }

        return new MessageBody(outRect.left, outRect.top, width, height, byteArray.length, byteArray).getBuf();
    }

}
