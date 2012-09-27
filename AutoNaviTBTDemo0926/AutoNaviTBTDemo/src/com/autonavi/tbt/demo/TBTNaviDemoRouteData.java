package com.autonavi.tbt.demo;

import android.app.Application;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.core.PoiItem;
import com.autonavi.tbt.CameraPoint;
import com.autonavi.tbt.NaviGuideItem;

public class TBTNaviDemoRouteData extends Application{
	
	/* searched string content */
	public static String searchName = null;
	/* selected destination string content */
	public static String destName = null;
	/* selected destination tel */
	public static PoiItem selectedPoi = null;
	
	/* selected destination coordinates */
	public static GeoPoint destPoint = null;
	
	/* Camera info */
	public static CameraPoint cameraPoint = null;

	/* NaviGuideItem */
	private NaviGuideItem []naviGuideItem=null;
	
	public void setNaviGuideItem(NaviGuideItem []naviGuideItem){
		this.naviGuideItem=naviGuideItem;
	}
	
	public NaviGuideItem[] getNaviGuideItem(){
		return naviGuideItem;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		CustemException ce = CustemException.getInstance();
		ce.init(getApplicationContext());
	}
};
