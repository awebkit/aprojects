package com.autonavi.tbt.demo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapController;
import com.amap.mapapi.map.MapView;
import com.amap.mapapi.map.Overlay;
import com.autonavi.tbt.Camera;
import com.autonavi.tbt.CameraPoint;
import com.autonavi.tbt.CarLocation;
import com.autonavi.tbt.DGNaviInfor;
import com.autonavi.tbt.PointList;
import com.autonavi.tbt.TBT;
import com.autonavi.tbt.TmcBar;
import com.autonavi.tbt.navi.FrameForTBT;
import com.autonavi.tbt.navi.NmeaData;
import com.autonavi.tbt.navi.SetGPSInfor;
import com.example.autotest.ScreenShotHelper;
import com.iflytek.tts.TtsService.Tts;

public class TBTNaviDemoMapView extends MapActivity implements LocationListener, OnTouchListener, OnGestureListener {
	
	//退出TBT导航
	private static final int EXIT_TBTNAVI = 0;
	private static final int ON_END_EMULATOR_NAVI = 0X00000001;
	private static final int ON_END_GPS_NAVI = 0x00000002;
	private static final int SHOW_MARQUEE_INFO_TEXT = 0x00000003;
	private static final int UPDATE_TBT_GUIDANCE_INFO = 0x00000004;
	private static final int NOTIFY_ROUTE_CALCULATION_RESULT = 0x00000005;
	private static final int CHOOSE_ROUTE_CALCULATION_CATELOG = 0x00000006;
	private static final int ON_CAR_LOCATION_CHANGE = 0x00000007;
	private static final int SHOW_TMC_BAR = 0x00000008;
	private static final int SHOW_LANE_INFO = 0x00000009;
	private static final int HIDE_LANE_INFO = 0x0000000a;
	private static final int SHOW_TRAFFIC_PANEL_INFO = 0x0000000b;
	private static final int HIDE_TRAFFIC_PANEL_INFO = 0x0000000c;
	private static final int SHOW_ROUTE_REQUEST_ING = 0x0000000d;
	private static final int SHOW_ROUTE_FOR_TMC_HINT = 0x0000000e;
	private static final int UPDATE_GPS_VIEW_INFO = 0x0000000f;
	private static final int SHOW_CROSS_VIEW_INFO = 0x00000010;
	private static final int HIDE_CROSS_VIEW_INFO = 0x000000011;
	private static final int START_GPS_NAVIGATION = 0x000000012;
	private static final int START_EMULATOR_NAVIGATION = 0x000000013;
	private static final int UPDATE_GPS_VIEW_INFO_2 = 0x00000014;
	private static final int AUTOTEST_SCREENSHOT_MSG = 0x00000020;
	
	private SlidingDrawer mDrawer;
	private Boolean flag=false;
	private MapView mMapView;
	private MapController mMapController;
	static private GeoPoint pointCenter;
	static private GeoPoint pointStart;
	static private GeoPoint pointDest;
	
	private Button btnSearch;
	private Button btnRouteProfile;
	
	private TextView tvStreetName;
	private TextView tvDirection;
	private TextView tvGPSVelocity;

	private TextView tvDestDistance;
	private TextView tvDestETA;
	
	private ImageView ivGuideArrowBigView;
	private ImageView ivLaneInfoView;
	private ImageButton ibTrafficPanel;
	private ImageButton ibCross;
	
	// 地图上的导航信息
	private ImageView ivGuideArrowMap;
	private TextView tvSegmentRemainDistMap;
	private TextView tvNextStreetNameMap;
	private TextView tvRouteRemainDistMap;
	private TextView tvRouteRemainTimeMap;
	private RelativeLayout flGuideItemMap;
	
	private RelativeLayout rlTMCBarMap;
	private ImageView ivDistanceBarBgMap;
	private ImageView ivDistanceBarMap;
	private ImageView ivLaneInfoViewMap;
	private ImageButton ibTrafficPanelMap;
	private ImageButton ibCrossMap;
	
	private ImageView ivDistanceBarBg;
	private ImageView ivDistanceBar;
	
	private TextView tvSegmentRemainDist;
	private TextView tvMarqueeInfo;
	private final String VOICE_RES_FILE_NAME = "Resource.png";
	private String resPath;
	private double startLon, startLat;
	
	private ImageButton mDestLayer;
	private ImageButton btnNorthUp;
	private ImageButton ibBackToCar;
	private boolean mNorthUp = false;
	private boolean mMapCenter = true;

	//位置管理
	private LocationManager mLocatinManager;
	private MapPointOverlay overlay;
	private NaviLineOverlay naviOverlay;
	
	// 语音合成
	//private SynthesizerPlayer mSynthesizerPlayer = null;
	//private boolean mSpeakOver = false;
	//private TBTNaviDemoTTS mTTSPlayer;
	
	TBTNaviDemoRouteData mRouteData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		mRouteData = (TBTNaviDemoRouteData)getApplication();
		
		Bitmap tmpBmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_03_gray);
		BMP_LANEINFO_W = tmpBmp.getWidth();
		BMP_LANEINFO_H = tmpBmp.getHeight();		
		
		btnSearch = (Button)findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(onClick);
		
		btnRouteProfile = (Button)findViewById(R.id.btnRouteProfile);
		btnRouteProfile.setOnClickListener(onClick);
		
		tvStreetName = (TextView)findViewById(R.id.tvStreetName);
		tvDirection = (TextView)findViewById(R.id.tvDirection);
		tvGPSVelocity = (TextView)findViewById(R.id.tvGPSVelocity);
		tvDestDistance = (TextView)findViewById(R.id.tvDestDistance);
		tvDestETA = (TextView)findViewById(R.id.tvDestETA);

		ivGuideArrowBigView = (ImageView)findViewById(R.id.ivGuideArrowBigView);
		ivLaneInfoView = (ImageView)findViewById(R.id.ivLaneInfoView);
		ibTrafficPanel = (ImageButton) findViewById(R.id.ibTrafficPanel);
		ibTrafficPanel.setOnClickListener(onClick);
		ibCross = (ImageButton) findViewById(R.id.ibCross);
		ibCross.setOnClickListener(onClick);
		ivDistanceBar = (ImageView)findViewById(R.id.ivDistanceBar);
		ivDistanceBarBg = (ImageView)findViewById(R.id.ivDistanceBarBg);
			
		tvSegmentRemainDist = (TextView)findViewById(R.id.tvSegmentRemainDist);
		tvMarqueeInfo = (TextView)findViewById(R.id.tvMarqueeInfo);
		
		ivGuideArrowMap = (ImageView) findViewById(R.id.ivGuideArrowMap);
		tvSegmentRemainDistMap = (TextView) findViewById(R.id.tvSegmentRemainDistMap);
		tvNextStreetNameMap = (TextView) findViewById(R.id.tvNextStreetNameMap);
		tvRouteRemainDistMap = (TextView) findViewById(R.id.tvRouteRemainDistMap);
		tvRouteRemainTimeMap = (TextView) findViewById(R.id.tvRouteRemainTimeMap);
		flGuideItemMap = (RelativeLayout)findViewById(R.id.flGuideItemMap);
		
		rlTMCBarMap = (RelativeLayout)findViewById(R.id.rlTMCBarMap);;
		ivDistanceBarBgMap = (ImageView) findViewById(R.id.ivDistanceBarBgMap);
		ivDistanceBarMap = (ImageView) findViewById(R.id.ivDistanceBarMap);
		ivLaneInfoViewMap = (ImageView) findViewById(R.id.ivLaneInfoViewMap);
		ibTrafficPanelMap = (ImageButton) findViewById(R.id.ibTrafficPanelMap);
		ibTrafficPanelMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mTrafficPanelExists) {
					// 创建弹出窗口
					
					Dialog d = new TBTNaviDemoTrafficPanelDialog(TBTNaviDemoMapView.this, mTrafficPanelBitmap);
					d.show();
					ScreenShotHelper.registerDialog(d); 
				}
			}
		});
		ibCrossMap = (ImageButton) findViewById(R.id.ibCrossMap);
		ibCrossMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mCrossExists) {
					Dialog d = new TBTNaviDemoTrafficPanelDialog(TBTNaviDemoMapView.this, mCrossBitmap);
					d.show();
					ScreenShotHelper.registerDialog(d); 
				}
			}
		});
		
		mDrawer = (SlidingDrawer)findViewById(R.id.slidingdrawer);
		mDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener()
		  {
				private Button ibHandle = (Button) findViewById(R.id.handle);
			   @Override
			   public void onDrawerOpened() {
				    flag=true;		
				    //ibHandle.setBackgroundResource(R.drawable.arrow_down_float);
				    ibHandle.setText("关闭地图");
				    tvStreetName.setVisibility(View.INVISIBLE);
				    
				   // if (simNaviState || inGuideStatus)
				    if (simNaviState || inGuideStatus){
				    	flGuideItemMap.setVisibility(View.VISIBLE);
				    } else {
				    	flGuideItemMap.setVisibility(View.INVISIBLE);
				    }
				    {				    	
				    	refreshMapView();
				    }
				   
				    {
				    	updateTBTInfo();
				    }
				    
				    
		   }
		   
		  });
		mDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener(){
			
				private Button ibHandle = (Button) findViewById(R.id.handle);
			   @Override
			   public void onDrawerClosed() {
				    flag=false;
				    //ibHandle.setBackgroundResource(R.drawable.arrow_up_float);
				    ibHandle.setText("打开地图");
				    tvStreetName.setVisibility(View.VISIBLE);
			   }
			   
	
		   
		  });
		/*overlay=new MapPointOverlay(this);
		mDestLayer = (ImageButton) findViewById(R.id.ibDestLayer);
		mDestLayer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "点击地图选择目的地", Toast.LENGTH_LONG).show();
				mMapView.getOverlays().add(overlay);
			}
		}); */
		
		btnNorthUp = (ImageButton) findViewById(R.id.ibNorthUpMap);
		btnNorthUp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				mNorthUp = !mNorthUp;
				
				refreshMapView();
			}
		});
		
		ibBackToCar = (ImageButton) findViewById(R.id.ibBackToCar);
		ibBackToCar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (pointCenter == null) {
					pointCenter = new GeoPoint(39976756, 116381907);					
				}

				mMapController.setCenter(pointCenter);
				mMapCenterCtrlByUserCount = 0;
			}
			
		});
		
		mMapView = (MapView)findViewById(R.id.mapView);
		//mMapView.setOnTouchListener(touchListener);
		mMapView.setOnTouchListener(this);
		mMapView.setLongClickable(true);
		mGestureDetector = new GestureDetector(this);
		mGestureDetector.setIsLongpressEnabled(true);
		mMapView.setVectorMap(true);
		//mMapView.setStreetView(true);
		mMapView.setBuiltInZoomControls(true); //设置启用内置的缩放控件
		mMapController = mMapView.getController(); //得到mMapView的控制权，可以用它控制和驱动平移和缩放
		mMapController.setZoom(17);
		mMapController.setCenter(new GeoPoint(39976756, 116381907));
		//mMapView.registerTrackballListener(new TrackballGestureDetector.OnTrackballListener() {
			
		//});
		 
		inflater = (LayoutInflater)getApplicationContext().getSystemService(
    	        Context.LAYOUT_INFLATER_SERVICE);
		
		mTbt = new TBT();
		mFrame = new FrameForTBT(this, mTbt);
		String strNum = "";
		TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        if(telephonyManager != null){
        	strNum = telephonyManager.getSimSerialNumber();
       	if(strNum == null || "".equals(strNum)){
        		strNum = telephonyManager.getDeviceId();
        	}
        }
		int iRet = mTbt.init(mFrame, Environment.getExternalStorageDirectory().getPath() + "/tbtdemo", "AN_Amap_ADR_FC", "0", strNum);
        if(iRet == 0)
        {
        	System.out.println("TBT初始化失败");
        	Toast t = Toast.makeText(TBTNaviDemoMapView.this, "TBT初始化失败!",           
					Toast.LENGTH_LONG);
        	t.show(); 
        	ScreenShotHelper.registerToast(t);
        	//return;
        }
        else
        {
        	System.out.println("TBT初始化成功");
        }
        
        //设置自车初始位置 
     
        pointCenter = new GeoPoint(39976756, 116381907);
        mTbt.setCarLocation(1, 116.381907, 39.976756);
         /*// 情报板Test...
         mTbt.setCarLocation(1, 121.477587, 31.197562);
    	 startLon=121.477587;
    	 startLat=31.197562;
    	 // 情报板Test...*/
    	 
    	 //mTbt.setEmulatorSpeed(60);
    	 mTbt.openTMC();
    	 mTbt.openTrafficRadio();
        
    	 initTTS();
    	 openGPS();
    	 
    	 // 创建TTS 线程
    	 HandlerThread handlerThread = new HandlerThread("handler_Thread");
         
         handlerThread.start();
         
         mTTSHandler = new TTSHandler(handlerThread.getLooper());


	//	requestRoute();
				
		//Test
		SetGPSInfor GPSInfor = new SetGPSInfor(this, mTbt);
		GPSInfor.start();
		
		ScreenShotHelper.setActivity(this);
		ScreenShotHelper.captureInsideActivity(this);
	}

	private TTSHandler mTTSHandler= null;
	
	private int nWidth, nHeight;

	/**
	 * 获取屏幕宽高
	 */
	private void getScreenInfo() {
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		nWidth = outMetrics.widthPixels;
		nHeight = outMetrics.heightPixels - 25;
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu, menu);
		ScreenShotHelper.registerMenu(menu);
		return true;
	}
	
	static boolean mRoutePrepared = false;
	
	public void setRoutePrepared(boolean b)
	{
		mRoutePrepared = b;
	}
	
	public void notifyRouteResult(int requestRouteState)
	{
		Message message = new Message();
		
	    message.what = NOTIFY_ROUTE_CALCULATION_RESULT;
	    message.obj = requestRouteState;
		
		mHandler.sendMessage(message);
	}

	// added begin by changgy for fresh GPS view for speed and dir item
	public void notifyGPSInfo(NmeaData nmea)
	{
		Message mess = new Message();

		mess.what = UPDATE_GPS_VIEW_INFO;
		mess.obj = nmea;

		mHandler.sendMessage(mess);
	}
	// added end by changgy
	   
    public void createScreenShotMessage() {
        Message message = new Message();
        message.what = AUTOTEST_SCREENSHOT_MSG;
        mHandler.sendMessage(message);
    }
    
	private static boolean inGuideStatus = false;//GPS导航
	private static boolean simNaviState = false;//模拟导航
	int navitype;
	
	private String calcRouteResultString(int routeResult){
		String rpHint;

		switch(routeResult){
		case 1:
			rpHint = "路径计算成功";
			break;
		case 2:
			rpHint = "网络超时";
			break;
		case 3:
			rpHint = "网络失败";
			break;
		case 4:
			rpHint = "请求参数错误";
			break;
		case 5:
			rpHint = "返回数据格式错误";
			break;
		case 6:
			rpHint = "起点周围没有道路";
			break;
		case 7:
			rpHint = "起点在步行街";
			break;
		case 8:
			rpHint = "终点周围没有道路";
			break;
		case 9:
			rpHint = "终点在步行街";
			break;
		case 10:
			rpHint = "途经点周围没有道路";
			break;
		case 11:
			rpHint = "途经点在步行街";
			break;
			
		default:
			rpHint = "未知错误";
		}
		
		return rpHint;
	}
	
	public void createNotifyRouteResultDialog(int routeResult){
		
		String str = calcRouteResultString(routeResult);
		
		PostText(str, 1);
		
		if (routeResult == 1) {
			//str += " :)\n戳Menu键试试吧..";
			((TBTNaviDemoRouteData)getApplication()).cameraPoint = mTbt.getAllCamera();
			
			if (!inGuideStatus)
			{
				 
				mAlertDialog = new AlertDialog.Builder(this).setTitle("请选择").setSingleChoiceItems(new String[]{"开始导航","模拟导航"}, 0, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						
						switch(whichButton)
						{   
						case 0:
							startGPSNavi();
							break;
						case 1:
							startEmulatorNavi();
							break;  
						}
						
						dialog.dismiss();						
					}
				}).create();
				mAlertDialog.show();
				ScreenShotHelper.registerDialog(mAlertDialog); 
			}
			
			naviOverlay = new NaviLineOverlay();
			if (flag) {
				
				refreshMapView();
				/*if (pointStart != null) {
					mMapController.setCenter(pointStart);
				}*/
			}
		}
		else {
			str += " :(\n请稍后再试试吧..";
		}

		if (mpDialog != null){
    		mpDialog.dismiss();
    	}
		
		if (routeResult != 1) {
			mAlertDialog = new AlertDialog.Builder(this).setTitle(str).setPositiveButton(R.string.ok, null).create();
			mAlertDialog.show();
			ScreenShotHelper.registerDialog(mAlertDialog);
		}
	}
	
	private AlertDialog mAlertDialog;
	
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		if (mRoutePrepared) {
			getMenuInflater().inflate(R.menu.menu, menu);
		}
		return super.onPrepareOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.startSimulator:// 开始模拟导航
		{
			Timer timer = new Timer();
			TimerTask task = new TimerTask(){

				@Override
				public void run() {
					Message message = new Message();
			    	
				    message.what = START_EMULATOR_NAVIGATION;
					
					mHandler.sendMessage(message);
				}
				
			};
			timer.schedule(task, 1000);		
		}
			break;
		case R.id.stopSimulator: //停止模拟导航
			mTbt.stopEmulatorNavi();
			simNaviState = false;
			onEmulatorNaviEnd();
			break;
		case R.id.startNavigation:// 开始导航
		{
			Timer timer = new Timer();
			TimerTask task = new TimerTask(){

				@Override
				public void run() {

					Message message = new Message();
			    	
				    message.what = START_GPS_NAVIGATION;
					
					mHandler.sendMessage(message);
				}
				
			};
			timer.schedule(task, 1000);	
		}
			break;
		case R.id.stopNavigation:  //停止导航
			mTbt.stopNavi();
			//((TBTNaviDemoRouteData)getApplication()).cameraPoint = null;
			inGuideStatus = false;
			mRoutePrepared = false;
			onGPSNaviEnd();
			break;		
		}
		return super.onOptionsItemSelected(item);

	}
	
	private void startGPSNavi()
	{
		inGuideStatus = true;
		if (showMapGuideInfo()) {
			flGuideItemMap.setVisibility(View.VISIBLE);
			refreshMapView();
		}
		mTbt.startGPSNavi();
	}
	
	private void startEmulatorNavi()
	{
		simNaviState = true;
		if (showMapGuideInfo()) {
			flGuideItemMap.setVisibility(View.VISIBLE);
			refreshMapView();
		}
		mTbt.startEmulatorNavi();
	}
	private void initTTS()
	{
		
		String ttsResourcePath = Environment.getExternalStorageDirectory().getPath() + "/tbtdemo/";
		File dir = new File(ttsResourcePath);
		if (!dir.exists())  
			dir.mkdir();
		
		
		resPath = ttsResourcePath + "/" + VOICE_RES_FILE_NAME;
		if (!(new File(resPath)).exists())
		{
			//copy Resource file to resPath
			try {
				InputStream is =  getAssets().open(VOICE_RES_FILE_NAME);
				FileOutputStream fos = new FileOutputStream(resPath);
				
				byte[] buffer = new byte[1024];  
	            int count = 0;  
	            while ((count = is.read(buffer)) > 0)  
	            {  
	                fos.write(buffer, 0, count);  
	            }  
	  
	            fos.close();  
	            is.close(); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Tts.JniCreate(resPath);
		Tts.JniSetParam(256, 1);
		Tts.JniSetParam(1280, 20); 
		
		/*
		if (mSynthesizerPlayer == null )
		{
			mSynthesizerPlayer = SynthesizerPlayer.createSynthesizerPlayer(
					this, "appid=12345678");
			mSynthesizerPlayer.setVoiceName("vivixiaomei");
		} 
		*/
		//mTTSPlayer.initTTS(this);
	}
	
	public void openGPS(){
		
		Date date = new Date();
		date.setTime(System.currentTimeMillis());
		String str = DateFormat.format("yyyyMMddkkmmss", date).toString();
		
		mGPSFileName = "/mnt/sdcard/GpsLog_"+str+".txt"; 

		mLocatinManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		if (mLocatinManager != null ) {
	        String bestProvider = mLocatinManager.getBestProvider(getCriteria(), true);
	        
	        if (bestProvider != null ){
	        	mLocatinManager.requestLocationUpdates(bestProvider, 1000, 0, this);
		        Location loc = mLocatinManager.getLastKnownLocation(bestProvider);
		        updateGPSView(loc);
	        }
	        else {
	        	Toast.makeText(getApplicationContext(), "GPS不可用，请打开设备GPS端口", Toast.LENGTH_LONG);
	        }
		}

	}
	
	public Criteria getCriteria(){
    	Criteria c = new Criteria();
    	c.setAccuracy(Criteria.ACCURACY_FINE);	//设置查询精度
    	//c.setAccuracy(Criteria.ACCURACY_COARSE);	//设置查询精度
    	c.setSpeedRequired(true);					//设置是否要求速度
    	c.setCostAllowed(true);					//设置是否允许产生费用
    	c.setBearingRequired(true);				//设置是否需要得到方向
    	c.setAltitudeRequired(false);				//设置是否需要得到海拔高度
    	c.setPowerRequirement(Criteria.POWER_LOW);	//设置允许的电池消耗级别
    	return c;									//返回查询条件
    }
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (location != null) {
			updateGPSView(location);
		}
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		updateGPSView(null);
		Toast.makeText(getApplicationContext(), "GPS不可用，请检查端口", Toast.LENGTH_LONG);
		
	}
	
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		if (provider != null) {
			Location location= mLocatinManager.getLastKnownLocation(provider);			
			updateGPSView(location);
			mLocatinManager.requestLocationUpdates(provider, 1000, 0, this);
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
        switch (status) {
        //GPS状态为可见时
        case LocationProvider.AVAILABLE:
        	System.err.printf("当前GPS状态为可见状态");
            break;
        //GPS状态为服务区外时
        case LocationProvider.OUT_OF_SERVICE:
        	System.err.printf("当前GPS状态为服务区外状态");
            break;
        //GPS状态为暂停服务时
        case LocationProvider.TEMPORARILY_UNAVAILABLE:
        	System.err.printf("当前GPS状态为暂停服务状态");
            break;
        }
		
	}
	String mGPSFileName = null;
	private void saveGps(Location loc) {
		Date date = new Date();
		date.setTime(loc.getTime());
		
		String str = ""+loc.getLongitude()+","+loc.getLatitude()
				+ "," + loc.getSpeed() * 3.6 +  "," + loc.getBearing()
				+ ", 0.0, " + (date.getYear()+1900)+ "," + (date.getMonth()+1)
				+ "," + date.getDate() + "," + date.getHours() + 
				"," + date.getMinutes()+ "," + date.getSeconds();

		try {
			//File file = new File(Environment.getExternalStorageDirectory(), "GpsTest.txt");
			FileWriter fw = new FileWriter(mGPSFileName , true);
			
			 // 将缓冲对文件的输出
	        BufferedWriter bw = new BufferedWriter(fw); 
	        bw.write(str);
	        bw.newLine();
	        
	        bw.flush();
	        bw.close();
	        fw.close();
	       
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}

	private void updateGPSView(Location loc) {
		if(loc != null) {
			saveGps(loc);
			
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			Date date = new Date();
			date.setTime(loc.getTime());
			
			//DecimalFormat format = new DecimalFormat("#0.0");			
			//tvGPSVelocity.setText( format.format(loc.getSpeed() * 3.6).toString() + "km/h");
			//tvDirection.setText(loc.hasBearing() ? calcDirection(loc.getBearing()) : "无方向");
//			tvGPSTime.setText(date.getHours()+ ":" + (date.getMinutes() < 10 ? "0" : "") + date.getMinutes());
			NmeaData nmeaData = new NmeaData();
			nmeaData.m_Longitude = loc.getLongitude();
			nmeaData.m_Latitude = loc.getLatitude();
			nmeaData.m_Altitude = loc.getAltitude();
			nmeaData.m_Speed = loc.getSpeed() * 3.6;
			nmeaData.m_Track = loc.getBearing();
			nmeaData.m_MagVariation = 45;
			nmeaData.m_Pdop = loc.getAccuracy();			// 位置精度参数
			nmeaData.m_Hdop = 1;			// 水平精度参数
			nmeaData.m_Vdop = 1;			// 垂直精度参数
			nmeaData.m_NumSats = 4;			// 星空图卫星个数
			nmeaData.m_FixedMode = 4;			// GPS定位质量
			nmeaData.m_BJYear = date.getYear()+1900;			// GPS(BJ)时间－－年
			nmeaData.m_BJMonth = date.getMonth()+1;			// GPS(BJ)时间－－月
			nmeaData.m_BJDay = date.getDate();			// GPS(BJ)时间－－日
			nmeaData.m_BJHour = date.getHours();			// GPS(BJ)时间－－时
			nmeaData.m_BJMinute = date.getMinutes();			// GPS(BJ)时间－－分
			nmeaData.m_BJSecond = date.getSeconds();			// GPS(BJ)时间－－秒
			nmeaData.m_ValidChar = 'A';		//定位成功与否的标志
			nmeaData.m_LastFixQuality = 1; 	// 前一次定位质量，0 无效坐标，1 有效坐标，2 DGPS更正坐标
			nmeaData.m_HasCoordEverBeenValid = true;	//是否曾经定位成功过
			
			Message msg = new Message();
			msg.what = UPDATE_GPS_VIEW_INFO_2;
			msg.obj = nmeaData;
			mHandler.sendMessage(msg);
		
			//mTbt.setGPSInfor(nmeaData);
			mTbt.setGPSInfor(1, nmeaData.m_Longitude, nmeaData.m_Latitude, nmeaData.m_Speed, nmeaData.m_Track,
    				nmeaData.m_BJYear, nmeaData.m_BJMonth, nmeaData.m_BJDay, nmeaData.m_BJHour, nmeaData.m_BJMinute, nmeaData.m_BJSecond);

		}
	}
	
	private String calcDirection(float d)
	{
		int degrees =  (int)d % 360;
		if (degrees >= 345 && degrees <= 15 )
		{
			return "北";
		}
		else if (degrees > 15 && degrees < 75)
		{
			return "东北";
		}
		else if (degrees >= 75 && degrees <= 105)
		{
			return "东";
		}
		else if (degrees > 105 && degrees < 165)
		{
			return "东南";
		}
		else if (degrees >= 165 && degrees <= 195)
		{
			return "南";
		}
		else if (degrees > 195 && degrees < 255)
		{
			return "西南";
		}
		else if (degrees >= 255 && degrees <= 285)
		{
			return "西";
		}
		else
		{
			return "西北";
		}		
	}
	
	private static final int ACTIVITY_SEARCH = 1;
	private static final int ACTIVITY_PROFILE = 2;
	private OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == btnSearch)
			{
				Intent intent = new Intent(getApplicationContext(), TBTNaviDemoSearch.class);
				startActivityForResult(intent, ACTIVITY_SEARCH);
			}
			
			if (v == btnRouteProfile)
			{
				if (mRoutePrepared)
				{
					((TBTNaviDemoRouteData)getApplication()).setNaviGuideItem(mTbt.getNaviGuideList());					
					Intent intent = new Intent(getApplicationContext(), TBTNaviDemoRouteProfile.class);
				//	Bundle bundle=new Bundle();s
				//	intent.putExtras(bundle);
					startActivityForResult(intent, ACTIVITY_PROFILE);
				}
			}
			
			if (v == ibTrafficPanel){
				if (mTrafficPanelExists) {
					// 创建弹出窗口
					
					Dialog d = new TBTNaviDemoTrafficPanelDialog(TBTNaviDemoMapView.this, mTrafficPanelBitmap);
					d.show();
					ScreenShotHelper.registerDialog(d);
				}
			}
			
			if (v == ibCross) {
				if (mCrossExists) {
					Dialog d = new TBTNaviDemoTrafficPanelDialog(TBTNaviDemoMapView.this, mCrossBitmap);
					d.show();
					ScreenShotHelper.registerDialog(d);
				}
			}
			
			if (v == ibTrafficPanelMap){
				if (mTrafficPanelExists) {
					// 创建弹出窗口
					
					Dialog d = new TBTNaviDemoTrafficPanelDialog(TBTNaviDemoMapView.this, mTrafficPanelBitmap);
					d.show();
					ScreenShotHelper.registerDialog(d);
				}
			}
			
			if (v == ibCrossMap) {
				if (mCrossExists) {
					Dialog d = new TBTNaviDemoTrafficPanelDialog(TBTNaviDemoMapView.this, mCrossBitmap);
					d.show();
					ScreenShotHelper.registerDialog(d);
				}
			}
		}
	};	
	private int routetype;
	double mDestLong = 0;
	double mDestLat = 0;
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
        ScreenShotHelper.setActivity(this);
        ScreenShotHelper.captureInsideActivity(this);
    	if (requestCode == ACTIVITY_SEARCH) {
            if (resultCode == RESULT_OK) {
                // 增加检索结果算路代码
            	mRoutePrepared = false;
            	//mTbt.stopNavi();
            	int[] geoPoint = data.getIntArrayExtra("DESTPOINT");
				pointDest = new GeoPoint((int) (geoPoint[0]),
						(int) (geoPoint[1]));
				mDestLong = geoPoint[1]/1E6;
				mDestLat = geoPoint[0]/1E6;
				
            	createChoiceDialog();
        }		
	}
  }   	
    private void  createChoiceDialog(){
    	
    	Message message = new Message();
    	
	    message.what = CHOOSE_ROUTE_CALCULATION_CATELOG;
		
		mHandler.sendMessage(message);
    	
    }
    private void createPathChoiceDialog() {
		int pathIndex = 0;

		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("路径计算方式选择").setSingleChoiceItems(new String[]{"避开拥堵","最优路径","快速路优先","距离优先","普通路优先"}, pathIndex, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				
				switch(whichButton)
				{   
				case 0:
					routetype=4;
					break;
				case 1:
					routetype=0;
					break;
				case 2:
					routetype=1;
					break;
				case 3:
					routetype=2;
					break;
				case 4:
					routetype=3;
					break;  
				}
				
				dialog.dismiss();
				
				if (mDestLong!=0 && mDestLat != 0 )
				{
					//Test
					//endList[0] = 121.461788;
					//endList[1] = 31.245167;
					//Test end...
					pointDest = new GeoPoint((int) (mDestLat * 1E6),
							(int) (mDestLong * 1E6));
					double[] endList = {mDestLong, mDestLat};
					System.out.printf( "dest[%f - %f]\n", mDestLong, mDestLat);
					mTbt.requestRoute(routetype, 1, endList);
					inGuideStatus = false;
					
					{
						// 路径计算中...
						Message message = new Message();
						
					    message.what = SHOW_ROUTE_REQUEST_ING;
						
						mHandler.sendMessage(message);
					}
					
				
					//double[] endList = {121.471509, 31.206977};
					//mTbt.requestRoute(routetype, 1, endList);
					       			
					/*mMapView.getOverlays().clear();
					GeoPoint Point =getMapCenter();
					mMapController.setCenter(Point);
					mMapView.getOverlays().add(new NaviLineOverlay());*/
					refreshMapView();
				}
			}
		})/*.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (mDestLong!=0 && mDestLat != 0 )
				{
					//Test
					//endList[0] = 121.461788;
					//endList[1] = 31.245167;
					//Test end...
					pointDest = new GeoPoint((int) (mDestLat * 1E6),
							(int) (mDestLong * 1E6));
					double[] endList = {mDestLong, mDestLat};
					System.out.printf( "dest[%f - %f]\n", mDestLong, mDestLat);
					mTbt.requestRoute(routetype, 1, endList);
					
					{
						// 路径计算中...
						Message message = new Message();
						
					    message.what = SHOW_ROUTE_REQUEST_ING;
						
						mHandler.sendMessage(message);
					}
					
				
					//double[] endList = {121.471509, 31.206977};
					//mTbt.requestRoute(routetype, 1, endList);
					       			
					mMapView.getOverlays().clear();
					GeoPoint Point =getMapCenter();
					mMapController.setCenter(Point);
					mMapView.getOverlays().add(new NaviLineOverlay());
				}
			}
		})*/.create();

		alertDialog.show();
		ScreenShotHelper.registerDialog(alertDialog);
	}
    
	public void setCCP(double longitude, double latitude) {		
		pointCenter = new GeoPoint((int)(latitude * 1E6), (int)(longitude*1E6));
		if (flag) {			
			//mMapView.getOverlays().clear();
			refreshMapView();
			//mMapController.setCenter(pointCenter);
			//mMapView.setMapAngle(getCarDir());
		}
		
	}
	
	public void reroute()
	{
		{
			// 路径计算中...
			Message message = new Message();
			
		    message.what = SHOW_ROUTE_REQUEST_ING;
			
			mHandler.sendMessage(message);
		}
		mTbt.reroute();
	}
	
	public void tmcReroute()
	{
		{
			// 路径计算中...
			Message message = new Message();
			
		    message.what = SHOW_ROUTE_FOR_TMC_HINT;
			
			mHandler.sendMessage(message);
		}
		reroute();
	}
	
	Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what == SHOW_MARQUEE_INFO_TEXT){
            	String text = (String)msg.obj;
            	ShowText(text);
            }
            else if(msg.what == UPDATE_TBT_GUIDANCE_INFO){
            	updateTBTInfo();
            }
            else if(msg.what == NOTIFY_ROUTE_CALCULATION_RESULT){            	
            	int code = (Integer) msg.obj;	
            	createNotifyRouteResultDialog(code);
			   
            }
            else if(msg.what == CHOOSE_ROUTE_CALCULATION_CATELOG){
            	createPathChoiceDialog();
			   
            }
            else if (msg.what == ON_CAR_LOCATION_CHANGE) {
	
            	if (flag) {	
            		/*mMapView.getOverlays().clear();
            		//GeoPoint point = getMapCenter();     		            		  
            		mMapController.setCenter(pointCenter);
            		mMapView.setMapAngle(getCarDir());
            		mMapView.getOverlays().add(new NaviLineOverlay());*/
            		refreshMapView();
        		}
            }
            else if (msg.what == SHOW_TMC_BAR) {
            	// 显示动态光柱            	
            	if(mDTLength == 2000)
            	{
            		ivDistanceBarBg.setImageResource(R.drawable.layout_main_bg_2km);
            	}
            	else if (mDTLength == 4000)
            	{
            		ivDistanceBarBg.setImageResource(R.drawable.layout_main_bg_4km);
            	}
            	else if (mDTLength == 8000)
            	{
            		ivDistanceBarBg.setImageResource(R.drawable.layout_main_bg_8km);
            	}
            	
            	TmcBar tmpTmcBar = (TmcBar)msg.obj;
            	Bitmap bmpTmcBar =  TBTNaviDemoTmcBar.createTmcBarBitmap(tmpTmcBar);
            	ivDistanceBar.setImageBitmap(bmpTmcBar);
            	
            	if (showMapGuideInfo()) {
            		if(mDTLength == 2000)
                	{
                		ivDistanceBarBgMap.setImageResource(R.drawable.layout_main_bg_2km);
                	}
                	else if (mDTLength == 4000)
                	{
                		ivDistanceBarBgMap.setImageResource(R.drawable.layout_main_bg_4km);
                	}
                	else if (mDTLength == 8000)
                	{
                		ivDistanceBarBgMap.setImageResource(R.drawable.layout_main_bg_8km);
                	}
            		
            		ivDistanceBarMap.setImageBitmap(bmpTmcBar);
            		rlTMCBarMap.setVisibility(View.VISIBLE);
            	}
            }
            else if (msg.what == SHOW_LANE_INFO) {
            	// 显示车线信息
            	Bitmap bmpLaneInfo = (Bitmap)msg.obj;
            	ivLaneInfoView.setScaleType(ImageView.ScaleType.CENTER);
            	ivLaneInfoView.setImageBitmap(bmpLaneInfo);
            	ivLaneInfoView.setVisibility(View.VISIBLE);
            	if (showMapGuideInfo()) {
            		ivLaneInfoViewMap.setScaleType(ImageView.ScaleType.CENTER);
            		ivLaneInfoViewMap.setImageBitmap(bmpLaneInfo);
            		ivLaneInfoViewMap.setVisibility(View.VISIBLE);
            	}
            }
            else if (msg.what == HIDE_LANE_INFO) {
            	ivLaneInfoView.setVisibility(View.INVISIBLE);
            	if (showMapGuideInfo()) {
            		ivLaneInfoViewMap.setVisibility(View.INVISIBLE);
            	}
            }
            
            else if (msg.what == SHOW_TRAFFIC_PANEL_INFO) {
            	// 显示情报板缩略图
            	mTrafficPanelBitmap = (Bitmap)msg.obj;
            	
            	int width = mTrafficPanelBitmap.getWidth();
            	int height = mTrafficPanelBitmap.getHeight();
            	Matrix matrix = new Matrix();
            	float scaleWidth = ((float)ibTrafficPanel.getWidth() / width);
            	float scaleHeight = ((float)ibTrafficPanel.getHeight() / height);
            	matrix.postScale(scaleWidth, scaleHeight); 
            	Bitmap newbmp = Bitmap.createBitmap(mTrafficPanelBitmap, 0, 0, width, height, matrix, true);
            	ibTrafficPanel.setImageBitmap(newbmp);           	
            	
            	ibTrafficPanel.setVisibility(View.VISIBLE);
            	mTrafficPanelExists = true;
            	
            	if (showMapGuideInfo()) {
            		Matrix matrixMap = new Matrix();
            		matrixMap.postScale(((float)ibTrafficPanelMap.getWidth() / width), ((float)ibTrafficPanelMap.getHeight() / height)); 
                	Bitmap newbmpMap = Bitmap.createBitmap(mTrafficPanelBitmap, 0, 0, width, height, matrixMap, true);
                	ibTrafficPanelMap.setImageBitmap(newbmpMap);           	
                	
                	ibTrafficPanelMap.setVisibility(View.VISIBLE);
            	}
            	
            }
            else if (msg.what == HIDE_TRAFFIC_PANEL_INFO) {
            	mTrafficPanelExists = false;
            	mTrafficPanelBitmap = null;
            	ibTrafficPanel.setVisibility(View.INVISIBLE);
            	if (showMapGuideInfo()) {
            		ibTrafficPanelMap.setVisibility(View.INVISIBLE);
            	}
            }
            else if (msg.what == SHOW_ROUTE_REQUEST_ING) {
            
            	// 显示路径计算中
            	mpDialog = new ProgressDialog(TBTNaviDemoMapView.this);
            	mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            	mpDialog.setMessage("路径请求中...");
            	mpDialog.setCancelable(true);
            	mpDialog.show();
            	ScreenShotHelper.registerDialog(mpDialog);
            }
            else if (msg.what == SHOW_ROUTE_FOR_TMC_HINT) {
            	Toast t = Toast.makeText(getApplicationContext(), "前方路况变化，重新计算路线中。。。",
    					Toast.LENGTH_LONG);
            	t.show();
            	ScreenShotHelper.registerToast(t);
            }
			else if (msg.what == UPDATE_GPS_VIEW_INFO)
			{
				NmeaData nmea = (NmeaData)msg.obj;
				DecimalFormat format = new DecimalFormat("#0.0");
				tvGPSVelocity.setText(format.format(nmea.m_Speed).toString() + "km/h");
				tvDirection.setText(calcDirection((float)nmea.m_Track));
			}
			else if (msg.what == UPDATE_GPS_VIEW_INFO_2)
			{
				NmeaData nmea = (NmeaData)msg.obj;
				DecimalFormat format = new DecimalFormat("#0.0");
				tvGPSVelocity.setText(format.format(nmea.m_Speed).toString() + "km/h");
				tvDirection.setText(calcDirection((float)nmea.m_Track));
			}
            
			else if (msg.what == SHOW_CROSS_VIEW_INFO)
			{
				// show cross
				Bitmap bmp = (Bitmap)msg.obj;
				mCrossBitmap = bmp;
				// 显示缩略版扩大图
				int width = mCrossBitmap.getWidth();
            	int height = mCrossBitmap.getHeight();
            	Matrix matrix = new Matrix();
            	float scaleWidth = ((float)ibCross.getWidth() / width);
            	float scaleHeight = ((float)ibCross.getHeight() / height);
            	matrix.postScale(scaleWidth, scaleHeight); 
            	Bitmap newbmp = Bitmap.createBitmap(mCrossBitmap, 0, 0, width, height, matrix, true);
            	ibCross.setImageBitmap(newbmp);           	
            	
            	ibCross.setVisibility(View.VISIBLE);
            	mCrossExists = true;
            	//if (showMapGuideInfo()) 
            	{
            		Matrix matrixMap = new Matrix();
            		matrixMap.postScale(((float)ibCrossMap.getWidth() / width), ((float)ibCrossMap.getHeight() / height)); 
                	Bitmap newbmpMap = Bitmap.createBitmap(mCrossBitmap, 0, 0, width, height, matrixMap, true);
                	ibCrossMap.setImageBitmap(newbmpMap);  
            		
                	
                	ibCrossMap.setVisibility(View.VISIBLE);
            	}
			}
			else if (msg.what == HIDE_CROSS_VIEW_INFO) {
				// hide cross
				mCrossBitmap = null;
				ibCross.setVisibility(View.INVISIBLE);
				mCrossExists = false;
				if (showMapGuideInfo()) {
					ibCrossMap.setVisibility(View.INVISIBLE);
				}
			}
			else if (msg.what == ON_END_EMULATOR_NAVI) {
				
				tvStreetName.setText("");
				tvDestDistance.setText(calcRestDistanceString(mTbt.getRouteLength()));
				tvDestETA.setText(calcRestTimeString(mTbt.getRouteTime()));
				tvSegmentRemainDist.setText("");
				ivGuideArrowBigView.setImageBitmap(null);
				ivDistanceBar.setImageBitmap(null);
				ibCross.setVisibility(View.INVISIBLE);
				ibTrafficPanel.setVisibility(View.INVISIBLE);
				ivLaneInfoView.setVisibility(View.INVISIBLE);

				ivLaneInfoViewMap.setVisibility(View.INVISIBLE);
				ibTrafficPanelMap.setVisibility(View.INVISIBLE);
				ibCrossMap.setVisibility(View.INVISIBLE);

				flGuideItemMap.setVisibility(View.INVISIBLE);
				rlTMCBarMap.setVisibility(View.INVISIBLE);
				
			}
			else if (msg.what == ON_END_GPS_NAVI) {				
				tvStreetName.setText("");
				tvDestDistance.setText("");
				tvDestETA.setText("");
				tvSegmentRemainDist.setText("");
				ivGuideArrowBigView.setImageBitmap(null);
				ivDistanceBar.setImageBitmap(null);
				ibCross.setVisibility(View.INVISIBLE);
				ibTrafficPanel.setVisibility(View.INVISIBLE);
				ivLaneInfoView.setVisibility(View.INVISIBLE);

				ivLaneInfoViewMap.setVisibility(View.INVISIBLE);
				ibTrafficPanelMap.setVisibility(View.INVISIBLE);
				ibCrossMap.setVisibility(View.INVISIBLE);
				
				flGuideItemMap.setVisibility(View.INVISIBLE);
				rlTMCBarMap.setVisibility(View.INVISIBLE);
			}
			else if (msg.what == START_GPS_NAVIGATION) {
				startGPSNavi();
			}
			else if (msg.what == START_EMULATOR_NAVIGATION) {
				startEmulatorNavi();
            } else if (msg.what == AUTOTEST_SCREENSHOT_MSG) {
                ScreenShotHelper.captureImage(true);
            }
        }
    };
    
    private boolean showMapGuideInfo()
    {
    	if (!flag) return false;
    	if (inGuideStatus || simNaviState)
    	{
    		return true;
    	}
    	return false;
    }
    private ProgressDialog mpDialog;
    
	private static Bitmap mTrafficPanelBitmap = null;    
	private static boolean mTrafficPanelExists = false; 
	
	private static Bitmap mCrossBitmap = null;
	private static boolean mCrossExists = false;
	
	public byte[] convertColorToByte(int []color){
			
		int length = color.length;
		if (length == 0){
			return null;
		}

		byte[] data = new byte[length * 3];
		for(int i = 0; i < length; ++i){
			data[i * 3] = (byte) ((color[i] >> 16) & 0xFF); 
			data[i * 3+ 1] = (byte) ((color[i] >> 8) & 0xFF); 
			data[i * 3+ 2] = (byte) (color[i] & 0x000000FF); 
		}	
		return data;
		
	}
	private String calcRestDistanceString(int restDistance) {
		String restDistanceStr;
		float kilometer = (float) restDistance / 1000;
		kilometer = (float) (Math.round(kilometer * 10)) / 10;

		if (restDistance >= 1000) {
			int kio = Math.round(kilometer);
			restDistanceStr = "" + kio + "km";
			return restDistanceStr;
		}

		restDistanceStr = "" + restDistance + "m";
		return restDistanceStr;
	}
	
	private String calcRestTimeString(int restTime) {
		String restTimeStr;
		float minutes = (float) restTime / 60;
		minutes = (float)(Math.round(minutes * 10)) / 10 ;
		
		if (restTime >= 60){
			int mio = Math.round(minutes);
			restTimeStr = "" + mio + "min";
		} else {
			restTimeStr = "" + restTime + "sec";
		}
		
		return restTimeStr;
		
	}
	public int getCarDir() {
		int dir = 0;
		if (carLocation != null && !simNaviState) {
			dir = carLocation.m_CarDir;
		} else {
			dir = carDir;
		}
		return dir;
	}
    private void updateTBTInfo()
    {
    	//if (inGuideStatus || simNaviState) 
    	{
	    	if (stDGNaviInfor.m_CurRoadName != null)
	    		tvStreetName.setText(stDGNaviInfor.m_CurRoadName + "->" + stDGNaviInfor.m_NextRoadName);
	    	tvDestDistance.setText(calcRestDistanceString(stDGNaviInfor.m_RouteRemainDis));
	    	tvDestETA.setText(calcRestTimeString(stDGNaviInfor.m_RouteRemainTime));
	    	tvSegmentRemainDist.setText(calcRestDistanceString(stDGNaviInfor.m_SegRemainDis));
	    	
	    	int resId = 0;
	    	switch(stDGNaviInfor.m_Icon)
	    	{
	    	case 9: //直行
	    		resId = R.drawable.layout_guide_turn_03_big;
	    		break;
	    	case 2: //左转
	    		resId = R.drawable.layout_guide_turn_06_big;	
	    		break;
	    		
	    	case 3: //右转
	    		resId = R.drawable.layout_guide_turn_07_big;
	    		break;    		
	    		
	    	case 4: //左前
	    		resId = R.drawable.layout_guide_turn_05_big;
	    		break;
	    		
	    	case 5: //右前
	    		resId = R.drawable.layout_guide_turn_04_big;
	    		break;
	    		
	    	case 6: //左后
	    		resId = R.drawable.layout_guide_turn_01_big;
	    		break;
	    	case 7: //右后
	    		resId = R.drawable.layout_guide_turn_02_big;
	    		break;
	    		
	    	case 8: //左转掉头
	    		resId = R.drawable.layout_guide_turn_08_big;
	    		break;
	    		
	    	case 1: // 自车
	    		resId = R.drawable.layout_guide_turn_21_big;
	    		break;
	    		
	    	case 10: // 到达途径地
	    		resId = R.drawable.layout_guide_turn_22_big;
	    		break;
	    		
	    	case 11: // 进入环岛
	    	case 12: // 驶出环岛
	    		resId = R.drawable.layout_guide_turn_23_big;
	    		break;
	    		
	    	case 13: // 到达服务区
	    		resId = R.drawable.layout_guide_turn_18_big;
	    		break;
	    		
	    	case 14: // 到达收费站
	    		resId = R.drawable.layout_guide_turn_24_big;
	    		break;
	    		
	    	case 15: // 到达目的地
	    		resId = R.drawable.layout_guide_turn_20_big;
	    		break;
	    		
	    	case 16: // 进入隧道
	    		resId = R.drawable.layout_guide_turn_19_big;
	    		break;
	    		
	    	default:
	    		System.err.printf("invalid guidance icon---[%d]", stDGNaviInfor.m_Icon);
	    		ivGuideArrowBigView.setImageResource(0);
	    		resId = 0;
	    		break;    	
	    	}
	    	ivGuideArrowBigView.setImageResource(resId);
	    	
	    	//if (simNaviState) 
	    	{
	    		if (stDGNaviInfor.m_Longitude!= 0 && stDGNaviInfor.m_Latitude != 0) {
	    			setCCP(stDGNaviInfor.m_Longitude, stDGNaviInfor.m_Latitude);
	    		}
	    	}
	    	
	    	//if (flag && flGuideItemMap.getVisibility() == View.VISIBLE) {
	    	if (showMapGuideInfo()) {
	    		if (resId != 0xffff) {
	    			ivGuideArrowMap.setImageResource(resId);
	    		}else {
	    			ivGuideArrowMap.setImageResource(0);
	    		}
	    		tvSegmentRemainDistMap.setText(calcRestDistanceString(stDGNaviInfor.m_SegRemainDis));
	    		tvNextStreetNameMap.setText(stDGNaviInfor.m_NextRoadName);
	    		tvRouteRemainDistMap.setText(calcRestDistanceString(stDGNaviInfor.m_RouteRemainDis));
	    		tvRouteRemainTimeMap.setText(calcRestTimeString(stDGNaviInfor.m_RouteRemainTime));
	    	}
    	}
    }
     
   

	private CarLocation carLocation;

	// 更新自车位置的状态常量

	public void onCarLocationChanged(CarLocation cl) {
		if (carLocation == null) {
			carLocation = new CarLocation();
		}
		this.carLocation.m_CarDir = cl.m_CarDir;
		this.carLocation.m_Latitude = cl.m_Latitude;
		this.carLocation.m_Longitude = cl.m_Longitude;
		this.carLocation.m_Speed = cl.m_Speed;
	    {
			// set CCP
			pointCenter = new GeoPoint((int)(cl.m_Latitude * 1E6), (int)(cl.m_Longitude*1E6));
			
			Message msg = new Message();
			msg.what = ON_CAR_LOCATION_CHANGE;	
			mHandler.sendMessage(msg);
			
		}
	}

	/** 自车的坐标 */
	private Point carPosition = new Point();
	private double lat = 0;
	private double lon = 0;
	private int carDir = 0;

	private DGNaviInfor stDGNaviInfor = new DGNaviInfor();

	public void updateNaviInfor(DGNaviInfor stDGNaviInfor) {
		lat = stDGNaviInfor.m_Latitude;
		lon = stDGNaviInfor.m_Longitude;
		carDir = stDGNaviInfor.m_CarDirection;
		this.stDGNaviInfor = stDGNaviInfor;

		//if (simNaviState|| inGuideStatus)
		{
			Message msg = new Message();
			msg.what = UPDATE_TBT_GUIDANCE_INFO;	
			mHandler.sendMessage(msg);
		}
		
		int start = 0;
		int length = mTbt.getRouteLength();
		
		start = mTbt.getRouteLength() - stDGNaviInfor.m_RouteRemainDis;
		
		
		if (stDGNaviInfor.m_RouteRemainDis > 8000)
		{
			mDTLength = 8000;
		}
		else if (stDGNaviInfor.m_RouteRemainDis > 4000)
		{
			mDTLength = 4000;
		}
		else// if (stDGNaviInfor.m_RouteRemainDis > 2000)
		{
			mDTLength = 2000;
		}
		
		TmcBar tmpTmcBar = mTbt.createTmcBar(start, mDTLength);
		if (tmpTmcBar != null) {
				Message msg = new Message();
				msg.what = SHOW_TMC_BAR;
				msg.obj = tmpTmcBar;
				mHandler.sendMessage(msg);
			}
	}
	private int mDTLength = 0;

	public DGNaviInfor getNaviInfo() {
		if (stDGNaviInfor != null)
			return stDGNaviInfor;
		else
			return null;
	}
	//获得自车位置
	public Point getCarPosition() {
		GeoPoint gp;
		if (carLocation != null ) {
			gp = new GeoPoint((int) (carLocation.m_Latitude), (int) (carLocation.m_Longitude));
		} else if (lat != 0 && lon != 0) {
			gp = new GeoPoint((int) (lat), (int) (lon));
		} else {
			gp = new GeoPoint((int) (startLat), (int) (startLon));
		}
		mMapView.getProjection().toPixels(gp, carPosition);
		return carPosition;
	}

	//获得地图中心点
	public GeoPoint getMapCenter() {
		GeoPoint gp=new GeoPoint();
		if (carLocation != null) {
			gp = new GeoPoint((int) (carLocation.m_Latitude* 1E6), (int) (carLocation.m_Longitude* 1E6));
		} else if (lat != 0 && lon != 0) {
			gp = new GeoPoint((int) (lat* 1E6), (int) (lon* 1E6));
		} else {
			gp = new GeoPoint((int) (startLat* 1E6), (int) (startLon* 1E6));
		}
		return gp;
	}
    
    public void ShowText(String text)
    {
    	tvMarqueeInfo.setText(text);
    }
    
	public void PostText(String text, int type)
	{
		Message message = new Message();
		if(type == 1){
			message.what = SHOW_MARQUEE_INFO_TEXT;
		}
		message.obj = text;
		mHandler.sendMessage(message);
	}


	public void playSound(String psound) {	
		
		if (psound != null) {
			Message message = mTTSHandler.obtainMessage();
	
			//message.what = 0x32345678;
			message.obj = psound;
			message.sendToTarget();
		}	
	}
	// 停止并退出 
	
	private void stopPlaySound() {
		Tts.JniStop();
		
	}
	public void onEmulatorNaviEnd()
	{
		simNaviState = false;
		Message message = new Message();
		message.what = ON_END_EMULATOR_NAVI;
		mHandler.sendMessage(message);
	}
	
	public void onGPSNaviEnd()
	{
		inGuideStatus = false;
		((TBTNaviDemoRouteData)getApplication()).cameraPoint = null;
		Message message = new Message();
		message.what = ON_END_GPS_NAVI;
		mHandler.sendMessage(message);

	}
	
    static public TBT mTbt;
    static public FrameForTBT mFrame;

    static {
    	System.loadLibrary("Aisound");
        System.loadLibrary("tbt");
    }

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub		
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			/*if (flag && pointCenter != null && mMapView.getMapCenter() != pointCenter) {
				mMapView.getController().setCenter(pointCenter);	
				return true;
			}
			else */
			{
				showDialog(EXIT_TBTNAVI);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case EXIT_TBTNAVI:
		    AlertDialog d = new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher).setTitle(R.string.quit).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					destroyTBTApplication();
				}
			}).setNegativeButton(R.string.cancel, null).create();
			ScreenShotHelper.registerDialog(d);
			return d;
		default:
			break;
		}

		return super.onCreateDialog(id);
	}
	
	protected void destroyTBTApplication() {

		mTbt.destroy();
		int PID = android.os.Process.myPid();
		android.os.Process.killProcess(PID);
		android.os.Process.sendSignal(PID, 9);

		TBTNaviDemoMapView.this.finish();
	}	
	private GeoPoint lineGeoPoint;
	private Path mPath;
    //获得路径点
	private Path getNaviPathLine() {
		if (mTbt != null) {
			mPath = new Path();
			Point linePoint = null;
			int segNum = mTbt.getSegmentNum();
			
			for (int i = 0; i < segNum; i++) {
				PointList pList = mTbt.getSegCoor(i);

				if (pList  != null ) { // add by changgy for check
					for (int j = 0; j < pList.m_Num * 2; j += 2) {
						lineGeoPoint = new GeoPoint((int) (pList.m_Point[j + 1] * 1E6), (int) (pList.m_Point[j] * 1E6));
						linePoint = new Point();
						mMapView.getProjection().toPixels(lineGeoPoint, linePoint);
						if (j == 0 && i == 0)
							mPath.moveTo(linePoint.x, linePoint.y);
						else
							mPath.lineTo(linePoint.x, linePoint.y);
					}
					if (linePoint != null)
						mPath.lineTo(linePoint.x, linePoint.y);
				}
				if (linePoint != null)
					mPath.lineTo(linePoint.x, linePoint.y);
			}
		}
		return mPath;
	}

	public class NaviLineOverlay extends Overlay {
		private Paint paint;
		private LinearGradient mLinearGradient;
		
	

		public NaviLineOverlay() {
			
			paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setStyle(Paint.Style.STROKE);
			paint.setAntiAlias(true);
			paint.setColor(Color.RED);
			paint.setStrokeWidth(6);
		}


		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
									
		    drawNaviLine(canvas);
		    drawStartPoint(canvas,mapView);
		    drawEndPoint(canvas,mapView);
		    drawCamera(canvas, mapView);
		    drawCar(canvas,mapView);
		    
		}
		//描画终点
		private void drawEndPoint(Canvas canvas,MapView mapView){
			if(pointDest!=null){
				int segNum = mTbt.getSegmentNum();
				int i=segNum-1;
				PointList pList = mTbt.getSegCoor(i);
				if (pList != null) { // add check by changgy
					int j=pList.m_Num * 2-2;
					GeoPoint Dest = new GeoPoint((int) (pList.m_Point[j + 1] * 1E6), (int) (pList.m_Point[j] * 1E6));
					Point screenPts = new Point();
					mapView.getProjection().toPixels(Dest, screenPts);
				    naviArrow = BitmapFactory.decodeResource(getResources(), R.drawable.end); 
				    naviArrowWidth = naviArrow.getWidth();
					naviArrowHeight = naviArrow.getHeight();
				    canvas.drawBitmap(naviArrow,screenPts.x - (naviArrowWidth >> 1), screenPts.y - (naviArrowHeight >> 1)+24, null);
				}
			}
		}
		//描画起点
		private void drawStartPoint(Canvas canvas,MapView mapView){
		
			if(pointDest!=null){				
			PointList pList = mTbt.getSegCoor(0);

			if (pList != null) {				
				pointStart = new GeoPoint((int) (pList.m_Point[1] * 1E6), (int) (pList.m_Point[0] * 1E6));				
				Point screenPts = new Point();
				mapView.getProjection().toPixels(pointStart, screenPts);
			    naviArrow = BitmapFactory.decodeResource(getResources(), R.drawable.start);
			    naviArrowWidth = naviArrow.getWidth();
				naviArrowHeight = naviArrow.getHeight();
			    canvas.drawBitmap(naviArrow,screenPts.x - (naviArrowWidth >> 1), screenPts.y - (naviArrowHeight >> 1)-24, null);
				}
			}
		}
		//描画自车
		private void drawCar(Canvas canvas,MapView mapView){
			Point screenPts = new Point();
			if(pointCenter==null){
				GeoPoint Center=getMapCenter();
				mapView.getProjection().toPixels(Center, screenPts);}
			else{
				mapView.getProjection().toPixels(pointCenter, screenPts);
			}
			
			
		    naviArrow = BitmapFactory.decodeResource(getResources(), R.drawable.navicar); 
		    naviArrowWidth = naviArrow.getWidth();
			naviArrowHeight = naviArrow.getHeight();
		    canvas.save();
		    if (mNorthUp){
		    	canvas.rotate(getCarDir(), screenPts.x, screenPts.y);
		    }
		    canvas.drawBitmap(naviArrow, screenPts.x - (naviArrowWidth >> 1), screenPts.y - (naviArrowHeight >> 1), null);
		    canvas.restore();  
		}
		//描画路径
		private void drawNaviLine(Canvas canvas) {
			Path path = getNaviPathLine();
			if (path != null) {
				canvas.drawPath(path, paint);
			}
		}
		
		// 描画电子眼
		private void drawCamera(Canvas canvas,MapView mapView) {
			CameraPoint cameraPoint = ((TBTNaviDemoRouteData)getApplication()).cameraPoint;
			if (cameraPoint == null) return;
			for (int i=0; i < cameraPoint.m_Num; i++) {
				Camera camera = cameraPoint.m_CameraPoint[i];
				Point screenPts = new Point();
				mapView.getProjection().toPixels(new GeoPoint((int)(camera.m_dLatitude * 1E6), (int)(camera.m_dLongitude * 1E6)), screenPts);
				
				//Bitmap icon;
				if (camera.m_iCameraType == 0) {
					naviArrow = BitmapFactory.decodeResource(getResources(), R.drawable.cam0);
				}
				else{
					naviArrow = BitmapFactory.decodeResource(getResources(), R.drawable.cam1);
				}
				
				canvas.drawBitmap(naviArrow, screenPts.x - (naviArrow.getWidth() >> 1), screenPts.y - (naviArrow.getHeight() >> 1), null);
			}
		}
		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			// TODO Auto-generated method stub
			return super.onTap(arg0, arg1);
		}
		
		
	
		 // 回收Bitmap对象资源
				 
		public void recyclyNaviArrow() {
			if (naviArrow != null && naviArrow.isRecycled()) {
				naviArrow.recycle();
				naviArrow = null;
			}
		}


		// 导航箭头图标 
		private Bitmap naviArrow;
		/** 导航箭头的宽度高度 */
		private int naviArrowWidth, naviArrowHeight;
	}
	
	public void showTrafficPanel(int size, byte[] pngBuf)
	{
		Bitmap bmp = BitmapFactory.decodeByteArray(pngBuf, 0, size);
		
		if (bmp == null) return;
		
		Message message = new Message();
		message.what = SHOW_TRAFFIC_PANEL_INFO;
		message.obj = bmp;
		
		mHandler.sendMessage(message);
	}
	
	public void hideTrafficPanel()
	{
		Message msg = new Message();
		msg.what = HIDE_TRAFFIC_PANEL_INFO;
		mHandler.sendMessage(msg);
	}
	
	Bitmap eraseArrayBackground(Bitmap bmpArray, Bitmap bmpBack)
	{
		int width = bmpArray.getWidth();
		int height = bmpArray.getHeight();
		
		int[] pixels = new int [width*height];
		int[] pixelsBack = new int[width*height];
		bmpArray.getPixels(pixels, 0, width, 0, 0, width, height);
		bmpBack.getPixels(pixelsBack, 0, width, 0, 0, width, height);
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] == 0xFFFF00FF) {
				pixels[i] = pixelsBack[i];
			}
		}
		Bitmap newBmp = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
		return newBmp;
	}
	
	public void showCross(byte[] pCrossBackBuf, byte[] pCrossArrowBuf,
			int iBackSize, int iArrowSize)
	{
		Bitmap bmpBack = BitmapFactory.decodeByteArray(pCrossBackBuf, 0, iBackSize);
		Bitmap bmpArray = BitmapFactory.decodeByteArray(pCrossArrowBuf, 0, iArrowSize);
		
		// 滤掉箭头的底色
		Bitmap bmpNewArray = eraseArrayBackground(bmpArray, bmpBack);
	
		Message message = new Message();
		
	    message.what = SHOW_CROSS_VIEW_INFO;
	    message.obj = bmpNewArray;
		
		mHandler.sendMessage(message);		
	}
	
	public void hideCross()
	{

		Message message = new Message();
		
	    message.what = HIDE_CROSS_VIEW_INFO;		
		mHandler.sendMessage(message);	
	}

	private static final int MAX_LANE_NUM = 8;
	private static final int SINGLE_LANE_MASK =0xf;
	private static int  BMP_LANEINFO_W = 24; // 车道信息图片的宽度
	private static int  BMP_LANEINFO_H = 28; // 车道信息图片的高度
	
	public void showLaneInfo(byte[] laneBackInfo, byte[] laneSelectInfo)
	{
		Bitmap bmp = createLaneInfoBitmap(laneBackInfo, laneSelectInfo);
		if (bmp == null ) return;
		Message message = new Message();
		
	    message.what = SHOW_LANE_INFO;
	    message.obj = bmp;
		
		mHandler.sendMessage(message);
	}
	public void hideLaneInfo()
	{
		Message msg = new Message();
		msg.what = HIDE_LANE_INFO;
		mHandler.sendMessage(msg);
		//ivLaneInfoView.setVisibility(View.INVISIBLE);
	}
		
	// 根据背景，前景信息拼接车线信息的Bitmap
	public Bitmap createLaneInfoBitmap(byte[] laneBackInfo, byte[] laneSelectInfo)
	{
		short sBackLaneIDs[] = new short[8];
		short sForeLaneIDs[] = new short[8];
		
		short sBackLaneNum = 0;
		short sForeLaneNum = 0;
		int i;
		for (i = 0; i < MAX_LANE_NUM; i++)
		{
			sBackLaneIDs[i] = SINGLE_LANE_MASK;
			sForeLaneIDs[i] = SINGLE_LANE_MASK;
		}
		for(i=0; i < MAX_LANE_NUM; i++)
		{
			if( SINGLE_LANE_MASK == laneBackInfo[i] ) break;
			sBackLaneIDs[i] = laneBackInfo[i];
			if( 13 == sBackLaneIDs[i] )  sBackLaneIDs[i] = 0;
			if( 14 == sBackLaneIDs[i] )  sBackLaneIDs[i] = 11;
			sBackLaneNum++;
		}
		for(i=0; i < MAX_LANE_NUM; i++)
		{
			//if( SINGLE_LANE_MASK == laneSelectInfo[i] ) break;

			sForeLaneIDs[i] = laneSelectInfo[i];
			if( 13 == sForeLaneIDs[i] )  sForeLaneIDs[i] = 0;
			if( 14 == sForeLaneIDs[i] )  sForeLaneIDs[i] = 11;

			sForeLaneNum++;
		}

		if( 0 == sBackLaneNum ) 
		{
			return null;
		}
		
		Bitmap newb = Bitmap.createBitmap( BMP_LANEINFO_W*sBackLaneNum, BMP_LANEINFO_H, Config.ARGB_8888 );
		Canvas cv = new Canvas( newb );
		for (i=0; i< sBackLaneNum; i++)
		{
			Bitmap bmp = getBackLaneBitmapById(sBackLaneIDs[i]);
			if (bmp != null)
			{
				cv.drawBitmap(bmp, BMP_LANEINFO_W*i, 0, null);
			}
		}
		
		for(i = 0; i < MAX_LANE_NUM ; i++)
		{
			if (sForeLaneIDs[i] == SINGLE_LANE_MASK)
				continue;
			Bitmap bmp = getForeLaneBitmapById(sBackLaneIDs[i], sForeLaneIDs[i]);

			if (bmp != null)
			{
				cv.drawBitmap(bmp, BMP_LANEINFO_W*(i), 0, null);
			}

		}
		
		cv.save( Canvas.ALL_SAVE_FLAG );
		cv.restore();
		
		return newb;
	}

	private Bitmap getForeLaneBitmapById(int backIndex, int foreIndex)
	{
		Bitmap bmp = null;
		switch(backIndex)
		{
		
		case 0: // 直行
			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_03);
			break;
		case 1: // 左转
			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_06);
			break;
		case 2: // 直行+左转
			if (foreIndex == 0)
			{
				// Ahead
				bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_09_1);
			}
			else if (foreIndex == 1)
			{
				// Left
				bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_09);
			}
			break;
		case 3: // 右转
			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_07);
			break;
		case 4: // 直行+右转
			if (foreIndex == 0)
			{
				// Ahead
				bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_10_1);
			}
			else if (foreIndex == 3)
			{
				// Left
				bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_10);
			}
			break;
		case 5:  // 左U turn
			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_08);
			break;
			
		case 6: // Left and right
			if (foreIndex == 1)
			{
				// left
				bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_11);
			}
			else if (foreIndex == 3)
			{
				// right
				bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_11_1);
			}
			break;
			
		case 7: // Ahead and left and right
			if (foreIndex == 0)
			{
				// Ahead
				bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_12_1);
			}
			else if (foreIndex == 1)
			{
				// Left
				bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_12);
			}
			else if (foreIndex == 3)
			{
				// right
				bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_12_2);
			}
			break;
			
		case 8: // Right U turn
			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_13);
			break;
			
		case 9: // Ahead and left U turn
			if (foreIndex == 0)
			{
				// Ahead
				bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_15_1);
			}
			else if (foreIndex == 5)
			{
				// Left U turn
				bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_15);
			}
			break;
			
		case 10: // Ahead and right U turn
			if (foreIndex == 0)
			{
				// Ahead
				bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_14_1);
			}
			else if (foreIndex == 8)
			{
				// Left U turn
				bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_14);
			}
			break;
			
		case 11: // Left and left U turn
			if (foreIndex == 1)
			{
				// Ahead
				bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_17_1);
			}
			else if (foreIndex == 5)
			{
				// Left U turn
				bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_17);
			}
			break;
			
		case 12: // Right and right U turn
			if (foreIndex == 3)
			{
				// Ahead
				bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_16_1);
			}
			else if (foreIndex == 8)
			{
				// Left U turn
				bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_16);
			}
			break;
		default:
			// 复杂车线，暂无图标
			//bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_0_gray);
			return null;
		}
		
		return bmp;
	}
	
	private Bitmap getBackLaneBitmapById(int index)
	{
		Bitmap bmp;
		switch(index)
		{
		
		case 0: // 直行
			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_03_gray);
			break;
		case 1: // 左转
			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_06_gray);
			break;
		case 2: // 直行+左转
				bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_09_gray);
			break;
		case 3: // 右转
			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_07_gray);
			break;
		case 4: // 直行+右转
			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_10_gray);
			break;
		case 5:  // 左U turn
			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_08_gray);
			break;
			
		case 6: // Left and right
			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_11_gray);
			break;
			
		case 7: // Ahead and left and right
			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_12_gray);
			break;
			
		case 8: // Right U turn
			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_13_gray);
			break;
			
		case 9: // Ahead and left U turn
			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_15_gray);
			break;
			
		case 10: // Ahead and right U turn
			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_14_gray);
			break;
			
		case 11: // Left and left U turn
			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_17_gray);
			break;
			
		case 12: // Right and right U turn
			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_16_gray);
			break;
		default:
			// 复杂车线，暂无图标
			//bmp = BitmapFactory.decodeResource(getResources(), R.drawable.turn_0_gray);
			return null;
		}
		
		return bmp;
	}
	
	
	// TTS Handler
	class TTSHandler extends Handler
    {
	    public TTSHandler()
	    {
	        
	    }
	    
	    public TTSHandler(Looper looper)
	    {
	        super(looper);
	    }
	    
	    @Override
	    public void handleMessage(Message msg)
	    {
	    	//mSpeakOver = false;
	        String str = (String)msg.obj;
	        Tts.JniSpeak(str);
	        /*mSynthesizerPlayer.playText(str, "bft=0",TBTNaviDemoMapView.this);
	        while(!mSpeakOver) {
				SystemClock.sleep(200);
			}
	        //mTTSPlayer.ttsSpeak(str);*/
	       //super.handleMessage(msg); 
	    }
    }
	
	private void refreshMapView() {
    	if (!mNorthUp)
		{
			//变成车头向上
			if (getCarDir() != 0) {
				Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.north_up_icon);
				Matrix m = new Matrix();
				m.setRotate((360 - getCarDir()), (float) bmp.getWidth() / 2, (float) bmp.getHeight() / 2);
				
				try {
	                Bitmap b2 = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
	                if (bmp != b2) {
	                	bmp.recycle();
	                	bmp = b2;
	                }
	            } catch (OutOfMemoryError ex) {
	                
	            }
				btnNorthUp.setImageBitmap(bmp);
			}
		}
		else
		{
			btnNorthUp.setImageResource(R.drawable.north_up_icon);
		}
    	
    	if (mMapCenterCtrlByUserCount != 0) {
    	}
    	else 
    	{
    	
	    	if (pointCenter != null){				    		
	    		mMapController.setCenter(pointCenter);
	    	}
	    	else 
	    	{
	    		mMapController.setCenter(new GeoPoint(39976756, 116381907));
	    	}
    	}
    	
    	mMapView.setMapAngle(mNorthUp ? 0 : getCarDir());
    	naviOverlay = new NaviLineOverlay();
    	if (mMapView.getOverlays() != null) {
    		mMapView.getOverlays().clear();
    		mMapView.getOverlays().add(naviOverlay);
    	}
    	
    	
	}
	
	public class MapPointOverlay extends Overlay{
	    private Context context;
	    private LayoutInflater inflater;
	    private View popUpView;
	    public MapPointOverlay(Context context){
	    	this.context=context;
	    	inflater = (LayoutInflater)context.getSystemService(
	    	        Context.LAYOUT_INFLATER_SERVICE);
	    }
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			// TODO Auto-generated method stub
			super.draw(canvas, mapView, shadow);                
		}

		@Override
		public boolean onTap(final GeoPoint point, final MapView view) {
			if(popUpView!=null){
				view.removeView(popUpView);
			}
		   // Projection接口用于屏幕像素点坐标系统和地球表面经纬度点坐标系统之间的变换
		    popUpView=inflater.inflate(R.layout.popup, null);
			TextView textView=(TextView) popUpView.findViewById(R.id.PoiName);
			textView.setText("点击设定目的地");
			MapView.LayoutParams lp;
			lp = new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,
					MapView.LayoutParams.WRAP_CONTENT,
					point,0,0,
					MapView.LayoutParams.BOTTOM_CENTER);
				view.addView(popUpView,lp);
				
			popUpView.setOnClickListener(new OnClickListener() {	
				@Override
				public void onClick(View v) {
					mDestLong = point.getLongitudeE6()/1E6;
					mDestLat = point.getLatitudeE6()/1E6;
					
					pointDest = point;
					
	            	createChoiceDialog(); 
	            	
	            	view.removeView(popUpView);
				}
			});
	        return super.onTap(point, view);
		}
	}
/*
	OnTouchListener touchListener = new OnTouchListener(){

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			return onTouchEvent(event);
		}
		
	};
*/	
	private GestureDetector mGestureDetector;
	private int mMapCenterCtrlByUserCount = 0;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_UP) {
			mMapCenterCtrlByUserCount += 1;
			Timer timer = new Timer();
			TimerTask timerTask = new TimerTask() {
	
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (mMapCenterCtrlByUserCount > 0) {
						mMapCenterCtrlByUserCount -= 1;
					}
				}
				
			};
			timer.schedule(timerTask, 5000);
		}
			
		return  mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}


	private LayoutInflater inflater;
    private View popUpView;

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		 int x = (int) e.getX();
         int y = (int) e.getY();

         final GeoPoint point = mMapView.getProjection().fromPixels(x, y);
         
         if(popUpView!=null){
				mMapView.removeView(popUpView);
			}
		   // Projection接口用于屏幕像素点坐标系统和地球表面经纬度点坐标系统之间的变换
		    popUpView=inflater.inflate(R.layout.popup, null);
			TextView textView=(TextView) popUpView.findViewById(R.id.PoiName);
			textView.setText("点击设定目的地");
			MapView.LayoutParams lp;
			lp = new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,
					MapView.LayoutParams.WRAP_CONTENT,
					point,0,0,
					MapView.LayoutParams.BOTTOM_CENTER);
				mMapView.addView(popUpView,lp);
				
			popUpView.setOnClickListener(new OnClickListener() {	
				@Override
				public void onClick(View v) {
					mDestLong = point.getLongitudeE6()/1E6;
					mDestLat = point.getLatitudeE6()/1E6;
					
					pointDest = point;
					
	            	createChoiceDialog(); 
	            	
	            	mMapView.removeView(popUpView);
				}
			});
	}



	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}

