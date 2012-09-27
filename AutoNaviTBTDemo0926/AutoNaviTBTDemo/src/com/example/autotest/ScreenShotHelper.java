package com.example.autotest;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.autonavi.tbt.demo.TBTNaviDemoMapView;

public class ScreenShotHelper {

    private final static boolean LOGD_ENABLED = Utils.LOGD_ENABLED;
    private static String LOG_TAG = "tbt";
    
    private static Activity mActivity;
    private static CaptureClerk mCaptureClerk;
    private static SparseArray<Dialog> mDialogArray;
    private static boolean mCaptureInside = false;
    private static Toast mToast;
    private static Menu mMenu;
    private static Bitmap mCapture; 
    
    public static void setActivity(Activity activity) {
        mActivity = activity;
        mCaptureInside = false;
    }
    
    public static void captureInsideActivity(Activity activity) {        
        mCaptureClerk = new CaptureClerk();
        mDialogArray = new SparseArray();
        mCaptureInside = true;
    }

    public static void registerDialog(Dialog d) {
        // TODO Auto-generated method stub
        mDialogArray.append(d.hashCode(), d);
    }

    public static void registerToast(Toast t) {
        // TODO Auto-generated method stub
        mToast = t;
    }

    public static void registerMenu(Menu menu) {
        // TODO Auto-generated method stub
        mMenu = menu;
    }

    public static void captureImage(boolean inside) {
        // TODO Auto-generated method stub
        if (mActivity == null)
            return;
        View v = mActivity.getWindow().getDecorView();
        int width = v.getWidth();
        int height = v.getHeight();
        if (width <= 0 || height <=0)
            return;
        Rect vRect = new Rect();
        v.getWindowVisibleDisplayFrame(vRect);
        mCapture = Utils.createPngScreenshot(v, v.getWidth(), v.getHeight() - vRect.top, vRect.top);
        
        drawDialog();
        
        drawToast(v, vRect);
        
        drawMenu(v, vRect);
        
        if (inside) {
            mCaptureClerk.setCaptureProduct(mCapture);
        }
    }

    private static void drawMenu(View v, Rect vRect) {
        // TODO Auto-generated method stub
        if (mMenu != null){
            View mv = InvokeMenuBuilder.getMenuView(mMenu, 0, (ViewGroup)v);
            if (mv.getWidth() > 0 && mv.isShown()){
                if (LOGD_ENABLED) Log.d(LOG_TAG, "======= prepare menu view2  ========" + mv.getHeight());
                Bitmap second = Utils.createPngScreenshot2(mv, mv.getWidth(), mv.getHeight(), 0);
                PointF fromPoint = new PointF(0, v.getHeight() - vRect.top - mv.getHeight());
                if (LOGD_ENABLED) Log.d(LOG_TAG, "======= prepare menu view2  ========" + fromPoint.x + " " + fromPoint.y);
                mCapture = Utils.mixtureBitmap2(mCapture, second, fromPoint, 0, (float) 0.9);
            }
        }
    }

    private static void drawToast(View v, Rect vRect) {
        // TODO Auto-generated method stub
        if (mToast != null){
            View tv = mToast.getView();
            if (tv.getWidth() > 0 && tv.isShown()){
                if (LOGD_ENABLED) Log.i(LOG_TAG, "==== " + tv.getWidth() + " " + tv.getHeight());
                Bitmap second = Utils.createPngScreenshot(tv, tv.getWidth(), tv.getHeight(), 0);
                if (LOGD_ENABLED) Log.d(LOG_TAG, "====== gravity " + mToast.getGravity() + " x y " + mToast.getXOffset() + " " 
                        + mToast.getYOffset() + " " + mToast.getHorizontalMargin() + " " + mToast.getVerticalMargin());
                PointF fromPoint = new PointF((v.getWidth() - tv.getWidth()) / 2, v.getHeight() - vRect.top - mToast.getYOffset() - tv.getHeight());
                if (LOGD_ENABLED) Log.i(LOG_TAG, "======= prepare menu view2  ========" + fromPoint.x + " " + fromPoint.y);
                mCapture = Utils.mixtureBitmap2(mCapture, second, fromPoint, 0, (float) 1.0);
            }        
        }
    }

    private static void drawDialog() {
        // TODO Auto-generated method stub
        if (mDialogArray == null)
            return;
        for (int i = 0; i < mDialogArray.size(); ++i){
            Dialog d = mDialogArray.valueAt(i);
            if (d != null && d.isShowing()){
                if (LOGD_ENABLED) Log.d(LOG_TAG, "======= dialog " + d.hashCode());
                Window w = d.getWindow();                
                View dialogView = w.getDecorView();
                
                Rect outRect = new Rect();
                dialogView.getWindowVisibleDisplayFrame(outRect);
                
                WindowManager.LayoutParams lp = w.getAttributes();
                PointF fromPoint = null;
                int g = lp.gravity;
                if (g == 48) {//
                    float x = lp.x;
                    float y = lp.y;
                    fromPoint = new PointF(x, y);                            
                } else if (g == 17){
                    DisplayMetrics outMetrics = new DisplayMetrics();
                    mActivity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
                    float x = (outMetrics.widthPixels - dialogView.getWidth())/ 2 + lp.x;
                    float y = (outMetrics.heightPixels - dialogView.getHeight() )/ 2 + lp.y;
                    fromPoint = new PointF(x, y);
                }
                
                int width = dialogView.getWidth();
                int height = dialogView.getHeight();
               
                height = height - outRect.top;
                if (width > 0 && height > 0){
                    Bitmap second = Utils.createPngScreenshot(dialogView, width, height, 0);
                    mCapture = Utils.mixtureBitmap(mCapture, second, fromPoint, lp.dimAmount);
                }
                break; //only one dialog
            }
        }
    }

    public static Bitmap getCapture() {
        // TODO Auto-generated method stub
        if (mCaptureInside){
            ((TBTNaviDemoMapView) mActivity).createScreenShotMessage();
            mCaptureClerk.getCaptureProduct();
        } else{
            captureImage(false);            
        }
        
        return mCapture;
    }
    
}
