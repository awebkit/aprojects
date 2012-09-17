package com.example.autotest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class ScreenSocket implements Runnable {
    private static String LOG_TAG = "tbt";
    
    private static int SOCKET_PORT = 54123;
    private static int KEYMAX = 246;
//    private static final HashMap S_MYKEYMAP = new HashMap();
    
    private Socket          client   = null;
    private ServerSocket    serverSocket   = null;
    private DataInputStream streamIn =  null;
    
    private ArrayList<AndroidEvent> mEventList;
    ScreenSocket(){
        mEventList = new ArrayList();
    }
    
    @Override
    public void run() {
        Log.i(LOG_TAG, "Creating socket thread ...");

        while (true) {
            try {
                serverSocket = new ServerSocket(SOCKET_PORT);

                client = serverSocket.accept();
                Log.i(LOG_TAG, "accept socket");
                open();

                boolean done = false; // True when receive exit.
                while (!done && client != null) {
                    try {
                        if (false) { // TEST
                            if (socketClosed()) {
                                Log.i(LOG_TAG, "======== client socket closed =======");
                                break;
                            }
                            String line = streamIn.readLine();
                            if (line == null) {
                                Log.i(LOG_TAG, "========no data, sleep 1s =======");
//                                if (client.isClosed() || !client.isConnected()
//                                        || client.isInputShutdown()) { //NO USE
//                                    Log.i(LOG_TAG, "======== client is closed =======");
//                                }
                                SystemClock.sleep(1000);
                                continue;
                            }
                            Log.i(LOG_TAG, "======== read msg =======" + line);
                        }
                        if (true) {
                            //Important
                            if (socketClosed()) {
                                Log.i(LOG_TAG, "======== client socket closed =======");
                                break;
                            }
                            
                            byte[] buffer = new byte[32];
                            // msg_id(4) + msg_len(4) + time(8) + devid(4)
                            int ret = streamIn.read(buffer, 0, 20);
                            if (ret == -1){
                                SystemClock.sleep(1000);
                                continue;
                            }
                            // type
                            byte[] shortbuffer = new byte[2];
                            byte[] intbuffer = new byte[4];
                            streamIn.read(buffer, 20, 2);
                            shortbuffer[0] = buffer[20];
                            shortbuffer[1] = buffer[21];
                            short type1 = Utils.ByteArraytoShort(shortbuffer);
                            // code
                            streamIn.read(buffer, 22, 2);
                            shortbuffer[0] = buffer[22];
                            shortbuffer[1] = buffer[23];
                            short code1 = Utils.ByteArraytoShort(shortbuffer);
                            // value
                            streamIn.read(buffer, 24, 4);
                            intbuffer[0] = buffer[24];
                            intbuffer[1] = buffer[25];
                            intbuffer[2] = buffer[26];
                            intbuffer[3] = buffer[27];
                            int value1 = Utils.ByteArraytoInt(intbuffer);
                            String msg = "read msg [" + type1 + " " + code1 + " " + value1 + "]";
                            Log.i(LOG_TAG, msg);
                            
                            doCmd(type1, code1, value1);
                        }
                    } catch (IOException e) {
                        Log.i(LOG_TAG, "socket thread exception, close");
                        done = true;
                    }
                }

                close();
            } catch (IOException e) {
                Log.i(LOG_TAG, "socket thread exception, exit");
                e.printStackTrace();
            }
        }
    }

    private void doCmd(short type, short code, int value) {
        Log.i(LOG_TAG, "=====do command [" + type + " " + code + " " + value + "]");
        
        if (type == 1 && code > 0 && code < KEYMAX) { //EV_KEY
            Log.i(LOG_TAG, "=====begin keyevent11");
            AndroidEvent event = new AndroidEvent(type, code, value);
            mEventList.add(event);
            Log.i(LOG_TAG, "=====end keyevent");
        } else if (type == 0) { //EV_SYN
            processAndroidEvent();
        } else if (type == 3) { //EV_REL
            // TODO
            AndroidEvent event = new AndroidEvent(type, code, value);
            mEventList.add(event);
        } else {
            Log.i(LOG_TAG, "bad command");
        }
    }

    private void processAndroidEvent() {
        if (mEventList.isEmpty())
            return;
        
        AndroidEvent aEvent = mEventList.get(0);
        if (aEvent.mType == 1){ //EV_KEY
            Instrumentation inst = new Instrumentation();
            if (aEvent.mValue == 1) {
                KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, aEvent.mCode);
                inst.sendKeySync(event);
            } else if (aEvent.mValue == 0) {
                KeyEvent event = new KeyEvent(KeyEvent.ACTION_UP, aEvent.mCode);
                inst.sendKeySync(event);
            } else {
                inst.sendKeyDownUpSync(aEvent.mCode);
            }
        } else if (aEvent.mType == 3){
            if (mEventList.size() != 2){
                return;
            }
            
            AndroidEvent bEvent = mEventList.get(1);
            Instrumentation inst = new Instrumentation();
            MotionEvent downEvent = MotionEvent.obtain(SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_DOWN, aEvent.mValue, bEvent.mValue, 0);
            MotionEvent upEvent = MotionEvent.obtain(SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_UP, aEvent.mValue, bEvent.mValue, 0);
            inst.sendPointerSync(downEvent);
            inst.sendPointerSync(upEvent);
        } else {
            Log.i(LOG_TAG, "not support now");
        }
        
        //Done, clear list.
        mEventList.clear();
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
        Log.i(LOG_TAG, "No data, close socket");
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

//    @Override
//    public void run() {
//        // TODO Auto-generated method stub
//
//        Log.i(LOG_TAG,"Creating socket thread111 ..." );
//        //Looper.prepare();                
////        try  
////        {     
//            ServerSocket serverSocket = null;
//            try {
//                serverSocket = new ServerSocket(SOCKET_PORT);
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            int i = 0;
//            while (true)  
//            {
//                Socket client = null;
//                try {
//                    client = serverSocket.accept();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }  
//                Log.i(LOG_TAG, "accept socket");
//                DataInputStream in = null;
//                try {
//                    in = new DataInputStream(client.getInputStream());
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
////                try  
////                {    
////                    DataInputStream in = new DataInputStream(client.getInputStream());
//                    try {
//                        Log.i(LOG_TAG, "000000000000000000000000");
//                        if (in.available() < 0)
//                            Log.i(LOG_TAG, "===== available ? NO");
//                        else
//                            Log.i(LOG_TAG, "===== available ? YES");
//                        Log.i(LOG_TAG, "===== avalable " + in.available());
//                        while(in != null){
//                            if (client == null)
//                                break;
//                            
//                            Log.i(LOG_TAG, "===== begin read =======");
////                            byte[] line = new byte[32];
////                            in.read(line, 0, 28);
//                            String line = in.readLine();
//                            if (line == null)
//                                break;
//                            Log.i(LOG_TAG, "======== read msg =======" + line);
////                            byte[] buffer = new byte[32];
////                            //msg_id
////                            in.read(buffer, 0, 4);
////                            //msg_len
////                            in.read(buffer, 4, 4);
////                            //time
////                            in.read(buffer, 8, 4);
////                            in.read(buffer, 12, 4);
////                            //type
////                            byte[] shortbuffer = new byte[2];
////                            byte[] intbuffer = new byte[4];
////                            in.read(buffer, 16, 2);
////                            shortbuffer[0] = buffer[16];
////                            shortbuffer[1] = buffer[17];
////                            short type1 =  Utils.ByteArraytoShort(shortbuffer);
////                            //code
////                            in.read(buffer, 18, 2);
////                            shortbuffer[0] = buffer[18];
////                            shortbuffer[1] = buffer[19];
////                            short code1 =  Utils.ByteArraytoShort(shortbuffer);
////                            //value
////                            in.read(buffer, 20, 4);
////                            intbuffer[0] = buffer[20];
////                            intbuffer[1] = buffer[21];
////                            intbuffer[2] = buffer[22];
////                            intbuffer[3] = buffer[23];
////                            int value1 =  Utils.ByteArraytoInt(intbuffer);
////                            String msg = "read msg [" + type1 + " " + code1 + " " + value1 + "]";
////                            Log.i(LOG_TAG, msg);
//                            
////                            if (type1 == 1 && code1 > 0 && code1 < KEYMAX) {
////                                Log.i(LOG_TAG, "=====begin keyevent11");
////                            Instrumentation inst = new Instrumentation();
////                            if (value1 == 1) {
////                                KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, code1);
////                                inst.sendKeySync(event);
////                            } else if (value1 == 0) {
////                                KeyEvent event = new KeyEvent(KeyEvent.ACTION_UP, code1);
////                                inst.sendKeySync(event);
////                            } else {
////                                inst.sendKeyDownUpSync(code1);
////                            }
////                                Log.i(LOG_TAG, "=====end keyevent");
////                            } else if (type1 == 3){
////                                //TODO
////                                Instrumentation inst = new Instrumentation();
////                                MotionEvent downEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 
////                                        MotionEvent.ACTION_DOWN, value1, value1, 0);
////                                MotionEvent upEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 
////                                        MotionEvent.ACTION_UP, value1, value1, 0);
////                                inst.sendPointerSync(downEvent);
////                                inst.sendPointerSync(upEvent);
////                            } else {
////                                Log.i(LOG_TAG, "bad command");
////                            }
//                                
////                        in = new DataInputStream(client.getInputStream());
//                        }
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
////                    in.close();  
//                }   
//                catch (Exception e)  
//                {   
//                    Log.i(LOG_TAG, "00000000000000");
//                    Log.i(LOG_TAG, e.getMessage());  
//                    e.printStackTrace();  
//                }
//                in.close();
//                finally  
//                {   
//                    client.close();  
//                    Log.i(LOG_TAG, "close client");  
//                }   
//            }
//        }
//        catch (Exception e)
//        {
//            Log.i(LOG_TAG, "exception occurs!");
//            e.printStackTrace();
//        }

        //Looper.loop();
//        Log.i(LOG_TAG, "Looper thread ends" );
//    }
    
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
