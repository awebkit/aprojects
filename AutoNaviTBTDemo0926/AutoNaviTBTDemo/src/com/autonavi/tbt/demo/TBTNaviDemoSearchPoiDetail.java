package com.autonavi.tbt.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.amap.mapapi.core.PoiItem;
import com.example.autotest.ScreenShotHelper;

public class TBTNaviDemoSearchPoiDetail extends Activity {
	
	private Button btnBack;
	private Button btnStartNavi;
	
	private TextView tvAddressDetail = null;
	private TextView tvTelephoneDetail = null;
	private TextView tvZipCodeDetail = null;
	private TextView tvSearchTitle = null;
	
	private PoiItem poi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_poi);
		
		btnBack = (Button) findViewById(R.id.btn_detail_back);
		btnBack.setOnClickListener(onClick);
		
		btnStartNavi = (Button) findViewById(R.id.btnNavi);
		btnStartNavi.setOnClickListener(onClick);
		
		poi = ((TBTNaviDemoRouteData)getApplication()).selectedPoi;
		
		tvSearchTitle = (TextView) findViewById(R.id.tvSearchTitle);
		tvSearchTitle.setText(poi.getTitle());
		tvAddressDetail = (TextView) findViewById(R.id.tvAddressDetail);
		tvAddressDetail.setText(poi.getSnippet());
		tvTelephoneDetail = (TextView) findViewById(R.id.tvTelephoneDetail);
		tvTelephoneDetail.setText(poi.getTel());
		tvZipCodeDetail = (TextView) findViewById(R.id.tvZipCodeDetail);
		tvZipCodeDetail.setText(poi.getAdCode());
		
		ScreenShotHelper.setActivity(this);
	}

	private OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == btnStartNavi)
			{
				((TBTNaviDemoRouteData)getApplication()).destName = poi.getTitle();
				((TBTNaviDemoRouteData)getApplication()).destPoint = poi.getPoint();				
				Intent intent = new Intent(getApplicationContext(), TBTNaviDemoSearch.class);
				setResult(RESULT_OK, intent);
        	    finish();
			}
			else if (v == btnBack)
			{
				Intent intent = new Intent(getApplicationContext(), TBTNaviDemoSearch.class);
				setResult(RESULT_CANCELED, intent);
				finish();
			}
			else
			{
				
			}
		}
	};
}
