package com.example.autotest;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.autonavi.tbt.demo.AutoNaviTBTDemoActivity;

public class ScreenSocket implements Runnable {
    private Activity mActivity;
    private static String LOG_TAG = "tbt";
    
    private static int SOCKET_PORT = 54123;
    private static int KEYMAX = 246;
//    private static final HashMap S_MYKEYMAP = new HashMap();
    
    ScreenSocket() {
    }
    
    public void setActivity(Activity activity){
        mActivity = activity;
    }
    
    @Override
    public void run() {
        // TODO Auto-generated method stub

        Log.i(LOG_TAG,"Creating socket thread111 ..." );
        //Looper.prepare();                
        try  
        {     
            ServerSocket serverSocket = new ServerSocket(SOCKET_PORT);
            int i = 0;
            while (true)  
            {
                Socket client = serverSocket.accept();  
                Log.i(LOG_TAG, "accept socket");  
                try  
                {    
                    DataInputStream in = new DataInputStream(client.getInputStream());
                    while(true && in.available() > 0){
                        if (client == null)
                            break;

                        byte[] buffer = new byte[32];
                        //msg_id
                        in.read(buffer, 0, 4);
                        //msg_len
                        in.read(buffer, 4, 4);
                        //time
                        in.read(buffer, 8, 4);
                        in.read(buffer, 12, 4);
                        //type
                        byte[] shortbuffer = new byte[2];
                        byte[] intbuffer = new byte[4];
                        in.read(buffer, 16, 2);
                        shortbuffer[0] = buffer[16];
                        shortbuffer[1] = buffer[17];
                        short type1 =  Utils.ByteArraytoShort(shortbuffer);
                        //code
                        in.read(buffer, 18, 2);
                        shortbuffer[0] = buffer[18];
                        shortbuffer[1] = buffer[19];
                        short code1 =  Utils.ByteArraytoShort(shortbuffer);
                        //value
                        in.read(buffer, 20, 4);
                        intbuffer[0] = buffer[20];
                        intbuffer[1] = buffer[21];
                        intbuffer[2] = buffer[22];
                        intbuffer[3] = buffer[23];
                        int value1 =  Utils.ByteArraytoInt(intbuffer);
                        String msg = "read msg [" + type1 + " " + code1 + " " + value1 + "]";
                        Log.i(LOG_TAG, msg);
                        
                        if (type1 == 1 && code1 > 0 && code1 < KEYMAX) {
                            Log.i(LOG_TAG, "=====begin keyevent");
                            Instrumentation inst = new Instrumentation();
                            if (value1 == 1) {
                                KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, code1);
                                inst.sendKeySync(event);
                            } else if (value1 == 0) {
                                KeyEvent event = new KeyEvent(KeyEvent.ACTION_UP, code1);
                                inst.sendKeySync(event);
                            } else {
                                inst.sendKeyDownUpSync(code1);
                            }
                            Log.i(LOG_TAG, "=====end keyevent");
                        } else if (type1 == 3){
                            //TODO
                            Instrumentation inst = new Instrumentation();
                            MotionEvent downEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 
                                    MotionEvent.ACTION_DOWN, value1, value1, 0);
                            MotionEvent upEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 
                                    MotionEvent.ACTION_UP, value1, value1, 0);
                            inst.sendPointerSync(downEvent);
                            inst.sendPointerSync(upEvent);
                        } else {
                            Log.i(LOG_TAG, "bad command");
                        }
                    }
                    in.close();  
                }   
                catch (Exception e)  
                {   
                    Log.i(LOG_TAG, "00000000000000");
                    Log.i(LOG_TAG, e.getMessage());  
                    e.printStackTrace();  
                }   
//                finally  
//                {   
//                    client.close();  
//                    System.out.println("close");  
//                }   
            }
        }
        catch (Exception e)
        {
            Log.i(LOG_TAG, "00000000000000");
            System.out.println(e.getMessage());
        }

        //Looper.loop();
        Log.i(LOG_TAG, "Looper thread ends" );
    }
    
//    protected void doCmd(String str) {
//        Log.i(LOG_TAG, "cmd is [" + str + "]");
//        String[] cmds = str.split(" ");
//        
//        int i = 0;
//        Log.i(LOG_TAG, cmds[i]);
//        
//        if (cmds[0].equals("KeyEvent")) {
//            doKeyEvent(cmds[1], cmds[2]);
//        } else if (cmds[0].equals("TouchEvent")){
//            doTouchEvent(cmds[1], cmds[2], cmds[3]);
//        } else {
//            Log.i(LOG_TAG, "wrong commnads");
//        }
//        
//    }

    private void doTouchEvent(String action, String xPos, String yPos) {
        Instrumentation inst = new Instrumentation();
        if (action.equals("DOWN")){
            MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 
                    MotionEvent.ACTION_DOWN, Integer.parseInt(xPos), Integer.parseInt(yPos), 0);
            inst.sendPointerSync(event);
        } else if (action.equals("UP")) {
            MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 
                    MotionEvent.ACTION_UP, Integer.parseInt(xPos), Integer.parseInt(yPos), 0);
            inst.sendPointerSync(event);
        } else {
            //DOWNUP
            MotionEvent downEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 
                    MotionEvent.ACTION_DOWN, Integer.parseInt(xPos), Integer.parseInt(yPos), 0);
            MotionEvent upEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 
                    MotionEvent.ACTION_UP, Integer.parseInt(xPos), Integer.parseInt(yPos), 0);
            inst.sendPointerSync(downEvent);
            inst.sendPointerSync(upEvent);
            Log.i(LOG_TAG, "===== Touch event: " + xPos + ", " + yPos);
        }
    }

//    private void doKeyEvent(String action, final String keycode) {
//        Log.i(LOG_TAG, "===== doKeyEvent ");
//        int key = (Integer) S_MYKEYMAP.get(keycode);
//        
//        Log.i(LOG_TAG, "===== key " + key);
//        Instrumentation inst = new Instrumentation();
//        if (action.equals("DOWN")) {
//            KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, key);
//            inst.sendKeySync(event);
//        } else if (action.equals("UP")) {
//            KeyEvent event = new KeyEvent(KeyEvent.ACTION_UP, key);
//            inst.sendKeySync(event);
//        } else {
//            inst.sendKeyDownUpSync(key);
//        }
//    }
    
//    private void initKeyMap() {
//        //hard key
//        S_MYKEYMAP.put("KEYCODE_BACK", KeyEvent.KEYCODE_BACK);
//        S_MYKEYMAP.put("KEYCODE_HOME", KeyEvent.KEYCODE_HOME);
//        S_MYKEYMAP.put("KEYCODE_MENU", KeyEvent.KEYCODE_MENU);
//        
//        //digit
//        S_MYKEYMAP.put("KEYCODE_0", KeyEvent.KEYCODE_0);
//        S_MYKEYMAP.put("KEYCODE_1", KeyEvent.KEYCODE_1);
//        S_MYKEYMAP.put("KEYCODE_2", KeyEvent.KEYCODE_2);
//        S_MYKEYMAP.put("KEYCODE_3", KeyEvent.KEYCODE_3);
//        S_MYKEYMAP.put("KEYCODE_4", KeyEvent.KEYCODE_4);
//        S_MYKEYMAP.put("KEYCODE_5", KeyEvent.KEYCODE_5);
//        S_MYKEYMAP.put("KEYCODE_6", KeyEvent.KEYCODE_6);
//        S_MYKEYMAP.put("KEYCODE_7", KeyEvent.KEYCODE_7);
//        S_MYKEYMAP.put("KEYCODE_8", KeyEvent.KEYCODE_8);
//        S_MYKEYMAP.put("KEYCODE_9", KeyEvent.KEYCODE_9);
//        
//        //characters
//        S_MYKEYMAP.put("KEYCODE_A", KeyEvent.KEYCODE_A);
//        S_MYKEYMAP.put("KEYCODE_B", KeyEvent.KEYCODE_B);
//        S_MYKEYMAP.put("KEYCODE_C", KeyEvent.KEYCODE_C);
//        S_MYKEYMAP.put("KEYCODE_D", KeyEvent.KEYCODE_D);
//        
//        S_MYKEYMAP.put("KEYCODE_E", KeyEvent.KEYCODE_E);
//        S_MYKEYMAP.put("KEYCODE_F", KeyEvent.KEYCODE_F);
//        S_MYKEYMAP.put("KEYCODE_G", KeyEvent.KEYCODE_G);
//        
//        S_MYKEYMAP.put("KEYCODE_H", KeyEvent.KEYCODE_H);
//        S_MYKEYMAP.put("KEYCODE_I", KeyEvent.KEYCODE_I);
//        S_MYKEYMAP.put("KEYCODE_J", KeyEvent.KEYCODE_J);
//        S_MYKEYMAP.put("KEYCODE_K", KeyEvent.KEYCODE_K);
//        
//        S_MYKEYMAP.put("KEYCODE_L", KeyEvent.KEYCODE_L);
//        S_MYKEYMAP.put("KEYCODE_M", KeyEvent.KEYCODE_M);
//        S_MYKEYMAP.put("KEYCODE_N", KeyEvent.KEYCODE_N);
//        S_MYKEYMAP.put("KEYCODE_O", KeyEvent.KEYCODE_O);
//        
//        S_MYKEYMAP.put("KEYCODE_P", KeyEvent.KEYCODE_P);
//        S_MYKEYMAP.put("KEYCODE_Q", KeyEvent.KEYCODE_Q);
//        S_MYKEYMAP.put("KEYCODE_R", KeyEvent.KEYCODE_R);
//        S_MYKEYMAP.put("KEYCODE_S", KeyEvent.KEYCODE_S);
//        
//        S_MYKEYMAP.put("KEYCODE_T", KeyEvent.KEYCODE_T);
//        S_MYKEYMAP.put("KEYCODE_U", KeyEvent.KEYCODE_U);
//        S_MYKEYMAP.put("KEYCODE_V", KeyEvent.KEYCODE_V);
//        S_MYKEYMAP.put("KEYCODE_W", KeyEvent.KEYCODE_W);
//        
//        S_MYKEYMAP.put("KEYCODE_X", KeyEvent.KEYCODE_X);
//        S_MYKEYMAP.put("KEYCODE_Y", KeyEvent.KEYCODE_Y);
//        S_MYKEYMAP.put("KEYCODE_Z", KeyEvent.KEYCODE_Z);
//        
//        S_MYKEYMAP.put("KEYCODE_ENTER", KeyEvent.KEYCODE_ENTER);
//    }
}
