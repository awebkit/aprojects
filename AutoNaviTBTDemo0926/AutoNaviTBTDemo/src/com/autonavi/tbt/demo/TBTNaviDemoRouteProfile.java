package com.autonavi.tbt.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.autonavi.tbt.NaviGuideItem;
import com.example.autotest.ScreenShotHelper;

public class TBTNaviDemoRouteProfile extends Activity {

	private Button btnBack;
	private TextView tvDestinationName;
	private ListView lvRouteProfile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
				
		btnBack = (Button) findViewById(R.id.btn_profile_back);
		btnBack.setOnClickListener(onClick);
		
		tvDestinationName = (TextView) findViewById(R.id.tvDestinationName);
		tvDestinationName.setText(((TBTNaviDemoRouteData)getApplication()).destName);
		
		lvRouteProfile = (ListView)findViewById(R.id.lvRoadList); 
		
		SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.profile_list_item,
                new String[]{"ivTurnInfo","tvCurrentStreetName","tvCurrentStreetLength"},
                new int[]{R.id.ivTurnInfo,R.id.tvCurrentStreetName,R.id.tvCurrentStreetLength});
        lvRouteProfile.setAdapter(adapter);
        ScreenShotHelper.setActivity(this);
	}

	private OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == btnBack)
			{
				Intent intent = new Intent(getApplicationContext(), TBTNaviDemoMapView.class);
				setResult(RESULT_CANCELED, intent);
				finish();
			}
			else
			{
				
			}
		}
	};
	
	private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
 
        NaviGuideItem[] naviGuideItem = ((TBTNaviDemoRouteData)getApplication()).getNaviGuideItem();
        
        for (int i = 0; i < naviGuideItem.length; i++)
        {
        	Map<String, Object> map = new HashMap<String, Object>();
            map.put("ivTurnInfo", calcTurnInfo(naviGuideItem[i].m_Icon));
            map.put("tvCurrentStreetName", naviGuideItem[i].m_Name);
            map.put("tvCurrentStreetLength", ""+ naviGuideItem[i].m_Length+"m");
            list.add(map);
        }         
        return list;
    }
	
	
	private Object calcTurnInfo(int icon)
	{
		Object obj = null;
		switch(icon)
		{
		case 9: //直行
			obj = R.drawable.layout_guide_turn_03_small;

    		break;
    	case 2: //左转
    		obj = R.drawable.layout_guide_turn_06_small;

    		break;
    		
    	case 3: //右转
    		obj = R.drawable.layout_guide_turn_07_small;

    		break;    		
    		
    	case 4: //左前
    		obj = R.drawable.layout_guide_turn_05_small;

    		break;
    		
    	case 5: //右前
    		obj = R.drawable.layout_guide_turn_04_small;

    		break;
    		
    	case 6: //左后
    		obj = R.drawable.layout_guide_turn_01_small;
    		break;


    	case 7: //右后
    		obj = R.drawable.layout_guide_turn_02_small;

    		break;    		
    	case 8: //左转掉头
    		obj = R.drawable.layout_guide_turn_08_small;

    		break;
    	case 1: // 自车
    		obj = R.drawable.layout_guide_turn_21_small;
    		break;
    		
    	case 10: // 到达途径地
    		obj = R.drawable.layout_guide_turn_22_small;
    		break;
    		
    	case 11: // 进入环岛
    	case 12: // 驶出环岛
    		obj = R.drawable.layout_guide_turn_23_small;
    		break;
    		
    	case 13: // 到达服务区
    		obj = R.drawable.layout_guide_turn_18_small;
    		break;
    		
    	case 14: // 到达收费站
    		obj = R.drawable.layout_guide_turn_24_small;
    		break;
    		
    	case 15: // 到达目的地
    		obj = R.drawable.layout_guide_turn_20_small;
    		break;
    		
    	case 16: // 进入隧道
    		obj = R.drawable.layout_guide_turn_19_small;
    		break;
    	default:
    		break;    	
    	}
		return obj;
	}
	
	/*
	 public class MyAdapter extends BaseAdapter{
		 
	        private LayoutInflater mInflater;
	         
	         
	        public MyAdapter(Context context){
	            this.mInflater = LayoutInflater.from(context);
	        }
	        @Override
	        public int getCount() {
	            // TODO Auto-generated method stub
	            return mData.size();
	        }
	 
	        @Override
	        public Object getItem(int arg0) {
	            // TODO Auto-generated method stub
	            return null;
	        }
	 
	        @Override
	        public long getItemId(int arg0) {
	            // TODO Auto-generated method stub
	            return 0;
	        }
	 
	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	             
	            ViewHolder holder = null;
	            if (convertView == null) {
	                 
	                holder=new ViewHolder();  
	                 
	                convertView = mInflater.inflate(R.layout.vlist2, null);
	                holder.img = (ImageView)convertView.findViewById(R.id.img);
	                holder.title = (TextView)convertView.findViewById(R.id.title);
	                holder.info = (TextView)convertView.findViewById(R.id.info);
	                holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
	                convertView.setTag(holder);
	                 
	            }else {
	                 
	                holder = (ViewHolder)convertView.getTag();
	            }
	             
	             
	            holder.img.setBackgroundResource((Integer)mData.get(position).get("img"));
	            holder.title.setText((String)mData.get(position).get("title"));
	            holder.info.setText((String)mData.get(position).get("info"));
	             
	            holder.viewBtn.setOnClickListener(new View.OnClickListener() {
	                 
	                @Override
	                public void onClick(View v) {
	                    showInfo();                 
	                }
	            });
	             
	             
	            return convertView;
	        }
	         
	    }
	    */
}
