package com.example.autotest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Instrumentation;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

public class ScreenSocket implements Runnable {
    
    private final static boolean LOGD_ENABLED = Utils.LOGD_ENABLED;    
    private static String LOG_TAG = "tbt";
    
    private static int SOCKET_PORT = 54123;
    
    private static int KEYMAX = 246;
    
    //for test
    private static int testi = 0;
    
    private static int emuX;
    private static int emuY;
    private static boolean touched = false;
    private static boolean pressured = false;
    
    private int mStatusBarHeight;
    
    private Socket          client   = null;
    private ServerSocket    serverSocket   = null;
    private DataInputStream streamIn =  null;
    
    ScreenSocket(Activity activity){
        
        //Caculate status bar height
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            mStatusBarHeight = activity.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            if (LOGD_ENABLED) Log.d(LOG_TAG, "get status bar height fail");
            e1.printStackTrace();
        }
        if (LOGD_ENABLED) Log.d(LOG_TAG, "======******* " + mStatusBarHeight);
    }
    
    @Override
    public void run() {
        if (LOGD_ENABLED) Log.d(LOG_TAG, "Creating socket thread ...");

        while (true) {
            try {
                serverSocket = new ServerSocket(SOCKET_PORT);
                
                client = serverSocket.accept();
                if (LOGD_ENABLED) Log.d(LOG_TAG, "accept socket");
                open();

                boolean done = false; // True when receive exit.
                while (!done && client != null) {
                    try {
                        if (false) { // TEST
                            if (socketClosed()) {
                                if (LOGD_ENABLED) Log.d(LOG_TAG, "======== client socket closed =======");
                                break;
                            }
                            String line = streamIn.readLine();
                            if (line == null) {
                                if (LOGD_ENABLED) Log.d(LOG_TAG, "========no data, sleep 1s =======");
                                SystemClock.sleep(1000);
                                continue;
                            }
                            if (LOGD_ENABLED) Log.d(LOG_TAG, "======== read msg  =======" + line);
                            //test
                            testi++;
                            if (testi == 1) {
//                                doCmd((short) 1, (short) KeyEvent.KEYCODE_BACK, 1);
                            }
                            if (testi == 2) {
//                                doCmd((short) 1, (short) KeyEvent.KEYCODE_BACK, 0);
                            }
                            //touch
                            if (testi == 3) {
//                                doCmd((short) 3, (short) 0, 250);
                            }
                            if (testi == 4) {
//                                doCmd((short) 3, (short)1, 585);
                            }
                            if (testi == 5) {
//                                doCmd((short)1, (short)330, 1);
                            }
                            if (testi == 6) {
//                                doCmd((short)3, (short)24, 1);
                            }
                            
                            //move
                            if (testi == 7) {
                                doCmd((short) 3, (short) 0, 250);
                            }
                            if (testi == 8) {
                                doCmd((short) 3, (short)1, 200);
                            }
                            if (testi == 9) {
                                doCmd((short)1, (short)330, 1);
                            }
                            if (testi == 10) {
                                doCmd((short)3, (short)24, 1);
                            }
                            if (testi == 11) {
                                doCmd((short) 3, (short) 0, 250);
                            }
                            if (testi == 12) {
                                doCmd((short) 3, (short)1, 400);
                            }
                            if (testi == 13) {
                                doCmd((short)1, (short)330, 0);
                            }
                            if (testi == 14) {
                                doCmd((short)3, (short)24, 0);
                            }
                        }
                        if (true) {
                            //Important
                            if (socketClosed()) {
                                if (LOGD_ENABLED) Log.d(LOG_TAG, "======== client socket closed =======");
                                break;
                            }
                            
                            if (false){
                            byte[] mybuffer = new byte[32];
                            int myret = streamIn.read(mybuffer, 0, 28);
                            if (myret == -1){
                                SystemClock.sleep(1000);
                                continue;
                            }
                            for (int i = 0; i < 28; ++i){
                                if (LOGD_ENABLED) Log.d(LOG_TAG, "== read [" + mybuffer[i] + "]");                                
                            }
                            }
                            if (true){
                            // msg_id(4) + msg_len(4) + time(8) + devid(4)
                            byte[] buffer = new byte[32];
                            
                            streamIn.readFully(buffer, 0, 28);
//                            if (LOGD_ENABLED) Log.d(LOG_TAG, "== read " + ret + " bytes");
                            // type
                            byte[] shortbuffer = new byte[2];
                            byte[] intbuffer = new byte[4];
//                            streamIn.read(buffer, 20, 2);
                            shortbuffer[0] = buffer[20];
                            shortbuffer[1] = buffer[21];
                            short type1 = Utils.ByteArraytoShort(shortbuffer);
                            // code
//                            streamIn.read(buffer, 22, 2);
                            shortbuffer[0] = buffer[22];
                            shortbuffer[1] = buffer[23];
                            short code1 = Utils.ByteArraytoShort(shortbuffer);
                            // value
//                            streamIn.read(buffer, 24, 4);
                            intbuffer[0] = buffer[24];
                            intbuffer[1] = buffer[25];
                            intbuffer[2] = buffer[26];
                            intbuffer[3] = buffer[27];
                            int value1 = Utils.ByteArraytoInt(intbuffer);
                            String msg = "read msg [" + type1 + " " + code1 + " " + value1 + "]";
                            if (LOGD_ENABLED) Log.d(LOG_TAG, msg);
                            
                            doCmd(type1, code1, value1);
                            }
                        }
                    } catch (IOException e) {
                        if (LOGD_ENABLED) Log.d(LOG_TAG, "socket thread exception, close");
                        done = true;
                    }
                }

                close();
            } catch (IOException e) {
                if (LOGD_ENABLED) Log.d(LOG_TAG, "socket thread exception, exit");
                e.printStackTrace();
            }
        }
    }

    private void doCmd(short type, short code, int value) {
        if (LOGD_ENABLED) Log.d(LOG_TAG, "=====do command [" + type + " " + code + " " + value + "]");
        
        if (false){
            Instrumentation inst = new Instrumentation();
            MotionEvent downEvent = MotionEvent.obtain(SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_DOWN, 250, 200, 0);
            
            MotionEvent moveEvent = MotionEvent.obtain(SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_MOVE, 250, 400, 0);

            
            
            MotionEvent upEvent = MotionEvent.obtain(SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_UP, 250, 400, 0);

            inst.sendPointerSync(downEvent);
            inst.sendPointerSync(moveEvent);
            inst.sendPointerSync(upEvent);
        }
        if (true) {
            if (type == 1) { // EV_KEY
                if (LOGD_ENABLED) Log.d(LOG_TAG, "=====begin keyevent ====");
                
                if (code == 330) {
                    touched = true;
                    if (LOGD_ENABLED) Log.d(LOG_TAG, "===== btn_touched ====");
                    return;
                }

                if (code > 0 && code < KEYMAX){
                    if (LOGD_ENABLED) Log.d(LOG_TAG, "===== key code is not right =====");
                    return;
                }
                
                Instrumentation inst = new Instrumentation();
                if (value == 1) {
                    KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, code);
                    inst.sendKeySync(event);
                } else if (value == 0) {
                    KeyEvent event = new KeyEvent(KeyEvent.ACTION_UP, code);
                    inst.sendKeySync(event);
                } else {
                    inst.sendKeyDownUpSync(code);
                }
                if (LOGD_ENABLED) Log.d(LOG_TAG, "=====end keyevent");
            } else if (type == 0) { // EV_SYN
                if (LOGD_ENABLED) Log.d(LOG_TAG, "===== ev_syn do nothing");
                // processAndroidEvent();
            } else if (type == 3) { // EV_ABS
                if (code == 0) {
                    emuX = value;
                } else if (code == 1) {
                    emuY = value;
                    if (pressured) {
                        Instrumentation inst = new Instrumentation();
                        MotionEvent moveEvent = MotionEvent.obtain(SystemClock.uptimeMillis(),
                                SystemClock.uptimeMillis(),
                                MotionEvent.ACTION_MOVE, emuX, emuY, 0);

                        inst.sendPointerSync(moveEvent);
                    }
                } else if (code == 24) {
                    if (touched == true){
                        touched = false;
                        
                        if (!isXYValidate()){
                            if (LOGD_ENABLED) Log.d(LOG_TAG, "=====The x y :"  + emuX + " " + emuY + " is not validate.");
                            return;
                        }
                        Instrumentation inst = new Instrumentation();
                        if (value == 1) {
                            pressured = true;
                            if (LOGD_ENABLED) Log.d(LOG_TAG, "==== down " + emuX + " " + emuY);
                            MotionEvent downEvent = MotionEvent.obtain(SystemClock.uptimeMillis(),
                                    SystemClock.uptimeMillis(),
                                    MotionEvent.ACTION_DOWN, emuX, emuY, 0);
                            inst.sendPointerSync(downEvent);
                        } else if (value == 0) {
                            pressured = false;
                            if (LOGD_ENABLED) Log.d(LOG_TAG, "==== move " + emuX + " " + emuY);

                            
                            if (LOGD_ENABLED) Log.d(LOG_TAG, "==== up " + emuX + " " + emuY);
                            MotionEvent upEvent = MotionEvent.obtain(SystemClock.uptimeMillis(),
                                    SystemClock.uptimeMillis(),
                                    MotionEvent.ACTION_UP, emuX, emuY, 0);

                            inst.sendPointerSync(upEvent);
                        }
                        return;
                    }
                    
                    if (LOGD_ENABLED) Log.d(LOG_TAG, "===== simulate move event begin ====");
                    
                    if (!isXYValidate()){
                        if (LOGD_ENABLED) Log.d(LOG_TAG, "=====The x y :"  + emuX + " " + emuY + " is not validate.");
                        return;
                    }
                    
                    Instrumentation inst = new Instrumentation();
                    MotionEvent upEvent = MotionEvent.obtain(SystemClock.uptimeMillis(),
                            SystemClock.uptimeMillis(),
                            MotionEvent.ACTION_MOVE, emuX, emuY, 0);

                    inst.sendPointerSync(upEvent);
                    if (LOGD_ENABLED) Log.d(LOG_TAG, "===== simulate move event end ====");
                }
            } else {
                if (LOGD_ENABLED) Log.d(LOG_TAG, "bad command");
            }
        }
    }

    private boolean isXYValidate() {
        // TODO Auto-generated method stub
        if (emuX < mStatusBarHeight){
            return false;
        }
        return true;
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
        if (LOGD_ENABLED) Log.d(LOG_TAG, "No data, close socket");
        if (client != null)
            client.close();
        if (streamIn != null)
            streamIn.close(); 
        if (serverSocket != null)
            serverSocket.close();
    }

    private void open() throws IOException {
        // TODO Auto-generated method stub
        streamIn = new DataInputStream(new BufferedInputStream(client.getInputStream()));
    }    
}
