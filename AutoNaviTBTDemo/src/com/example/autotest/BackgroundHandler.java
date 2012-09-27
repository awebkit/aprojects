package com.example.autotest;

import android.app.Activity;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundHandler {

    private final static boolean CAPTURE_TEST = Utils.CAPTURE_TEST;
    
    static HandlerThread sLooperThread;
    static ExecutorService mThreadPool;
    static CaptureServer mCaptureServer;
    static {
        sLooperThread = new HandlerThread("BackgroundHandler", HandlerThread.MIN_PRIORITY);
        sLooperThread.start();
        mThreadPool = Executors.newCachedThreadPool();
    }

    public static void init(Activity activity){
        //Start hotspot first because it needs time.
        if (!CAPTURE_TEST){
            execute(new Hotspot(activity));
        }
        //
        mCaptureServer = new CaptureServer(activity);
        execute(mCaptureServer);
        //
        execute(new ScreenSocket(activity));
    }
    
    public static void execute(Runnable runnable) {
        mThreadPool.execute(runnable);
    }

    public static Looper getLooper() {
        return sLooperThread.getLooper();
    }

    private BackgroundHandler() {}
}
