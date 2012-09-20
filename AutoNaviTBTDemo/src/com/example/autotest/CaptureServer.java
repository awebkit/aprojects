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
        Log.i(LOG_TAG, "Creating capture thread ...");

        while (true) {
            try {
                serverSocket = new ServerSocket(CAPTURE_PORT);

                client = serverSocket.accept();
                Log.i(LOG_TAG, "capture server accept socket");
                open();

                boolean done = false; // No meaning for done.
                while (!done && client != null) {
                    try {
                        if (true) {
                            if (socketClosed()) {
                                Log.i(LOG_TAG, "======== capture client socket closed =======");
                                break;
                            }                          

                            byte[] buffer = prepareCaptureMessage();
                            if (buffer != null) {
                                streamOut.write(buffer);
                                streamOut.flush();
                            }
                            SystemClock.sleep(2000);
                        }
                    } catch (IOException e) {
                        Log.i(LOG_TAG, "capture thread exception, close");
                        done = true;
                    }
                }
                close();
            } catch (IOException e) {
                Log.i(LOG_TAG, "capture thread exception, exit");
                e.printStackTrace();
            }
        }
    }

    private boolean socketClosed() {
        try {
            client.sendUrgentData(0);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return false;
        } catch (Exception se) {
            return true;
        }
    }

    private void close() throws IOException {
        Log.i(LOG_TAG, "No data, close capture server socket");
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

//    @Override
//    public void run() {
//        // TODO Auto-generated method stub
//        Log.i(LOG_TAG, "Create capture thread begin1122222");
//
//        while (true)
//        {
//            try
//            {
//                ServerSocket serverSocket = new ServerSocket(CAPTURE_PORT);
//                Socket client = serverSocket.accept();
//                Log.i(LOG_TAG, "accept ");
//                DataOutputStream os = new DataOutputStream(client.getOutputStream());
//                while (true) {
//                    //sleep 5s
//                    SystemClock.sleep(400);
//                    if (client == null)
//                        break;
//
//                    byte[] buffer = prepareCaptureMessage();
//                    if (buffer != null) {
//                        os.write(buffer);
//                        os.flush();
//                    }
//                }
//                os.close();
//
//            } catch (Exception e)
//            {
//                Log.i(LOG_TAG, "got exception begin?!!!");
//                e.printStackTrace();
//                Log.i(LOG_TAG, "got exception end ?!");
//            }
//        }
//    }
    
    private byte[] prepareCaptureMessage() {
        Log.i(LOG_TAG, "prepareCaptureMessage activity " + mActivity.hashCode());

        View v = mActivity.getWindow().getDecorView();
        if (v == null)
            return null;

        int width = v.getWidth();
        int height = v.getHeight();
        Rect outRect = new Rect();
        v.getWindowVisibleDisplayFrame(outRect);
        
        height = height - outRect.top;
        Log.i(LOG_TAG, "prepareCaptureMessage activity height:" + height);
        if (mInsideUI) {
            ((TBTNaviDemoMapView) mActivity).createTestMessage();

            Bitmap bmp = mCaptureClerk.getCaptureProduct();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, os);
            byte[] byteArray = os.toByteArray();

            pos++;
            Utils.saveScreenshot(mActivity, "/sdcard/jieping" + pos + ".jpg", bmp, true);

            return new MessageBody(width, height, byteArray.length, byteArray).getBuf();
        }

         Bitmap bmp = Utils.createScreenshot3(v, width, height);


        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, os);

        // TEST
        pos++;
        Utils.saveScreenshot(mActivity, "/sdcard/jieping" + pos + ".jpg", bmp, true);

        byte[] byteArray = os.toByteArray();
        Log.i(LOG_TAG, "prepareCaptureMessage end 222");
        return new MessageBody(width, height, byteArray.length, byteArray).getBuf();
    }

}
