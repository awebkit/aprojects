package com.autonavi.tbt.demo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class TBTNaviDemoTrafficPanelDialog extends Dialog {
	
	private Bitmap bmp;
	private ImageView imageView;
	
	public TBTNaviDemoTrafficPanelDialog(Context context, Bitmap bm) {
        super(context,R.style.dialog);
        this.bmp = bm;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.img_traffic_panel);
		
		imageView = (ImageView) findViewById(R.id.ivTrafficPanel);
		
		imageView.setImageBitmap(bmp);
		
		//init();
	}

	private void init() {
        
	}
}
