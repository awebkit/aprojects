package com.autonavi.tbt.demo;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.mapapi.core.AMapException;
import com.amap.mapapi.core.PoiItem;
import com.amap.mapapi.poisearch.PoiPagedResult;
import com.amap.mapapi.poisearch.PoiSearch;
import com.amap.mapapi.poisearch.PoiTypeDef;
import com.example.autotest.BackgroundHandler;
import com.example.autotest.ScreenShotHelper;

public class TBTNaviDemoSearch extends Activity {

	private Button btnBack;
	private Button btnSearch;	//search button
	private EditText edtPoi;
	private TextView tvTitleHistory;
	
	static String sPoiInputKeyWord;
	
	private ListView lvPoi;
	private List<PoiItem> list;
	private int routetype;
	private ProgressDialog progDialog;
	
	//
	private DataBaseAdapterForDestHistory mDbHelper;
	private boolean mDataFromHistory = true;
	private String LOG_TAG = "tbt";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		mDbHelper = new DataBaseAdapterForDestHistory(this);
        mDbHelper.open();
		
		btnBack = (Button) findViewById(R.id.btn_search_back);
		btnBack.setOnClickListener(onClick);
		
		btnSearch = (Button) findViewById(R.id.btn_search_normal);
		btnSearch.setOnClickListener(onClick);
		
		edtPoi = (EditText) findViewById(R.id.edt_search_edit);
		//edtPoi.setOnClickListener(onClick);
		
		progDialog = new ProgressDialog(this);
		
		tvTitleHistory = (TextView) findViewById(R.id.tvTitleHistory);
		
		lvPoi = (ListView)findViewById(R.id.lvPoi);  
		registerForContextMenu(lvPoi);
		lvPoi.setOnItemClickListener(new OnItemClickListener() {
		    @Override
			public void onItemClick(AdapterView<?> parent, View view,
		        int position, long id) {

		    	if (mDataFromHistory && destCursor != null) {
		    		destCursor.moveToFirst();
		    		destCursor.moveToPosition(position);
		    		
		    		((TBTNaviDemoRouteData)getApplication()).destName = destCursor.getString(
		    				destCursor.getColumnIndexOrThrow(DataBaseAdapterForDestHistory.KEY_TITLE));
		    		int rowId = destCursor.getInt(destCursor.getColumnIndexOrThrow(DataBaseAdapterForDestHistory.KEY_ROWID));
		    		int[] list = new int[2];
		    		list[0] = destCursor.getInt(destCursor.getColumnIndexOrThrow(DataBaseAdapterForDestHistory.KEY_LATITUDE));
					list[1] = destCursor.getInt(destCursor.getColumnIndexOrThrow(DataBaseAdapterForDestHistory.KEY_LONGITUDE));
					
					mDbHelper.updateDestination(rowId, ((TBTNaviDemoRouteData)getApplication()).destName, list[1], list[0], System.currentTimeMillis());
					destCursor.close();
					destCursor = null;
					mDbHelper.close();
					
					Intent intent = new Intent(getApplicationContext(), TBTNaviDemoMapView.class);
					Bundle bundle=new Bundle();
					bundle.putIntArray("DESTPOINT", list);
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
	        	    finish();
		    	}
		    	else
		    	{
			    	PoiItem poi = list.get(position);
			    	
			    	//((TBTNaviDemoRouteData)getApplication()).destName = poi.getTitle();
			    	((TBTNaviDemoRouteData)getApplication()).selectedPoi = poi;
			    	
			    	/*int[] list = new int[2];
					list[0] = poi.getPoint().getLatitudeE6();
					list[1] = poi.getPoint().getLongitudeE6();
									
					Intent intent = new Intent(getApplicationContext(), TBTNaviDemoMapView.class);
					Bundle bundle=new Bundle();
					bundle.putIntArray("DESTPOINT", list);
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
	        	    finish();
	        	    */
			    	
			    	Intent intent = new Intent(getApplicationContext(), TBTNaviDemoSearchPoiDetail.class);
			    	startActivityForResult(intent, ACTIVITY_POI_DETAIL);
		    	}
		    }
		  });
		
		fillDestinationHistoryData();
		Log.i(LOG_TAG, " ============TBTNaviDemoSearch === ");
		ScreenShotHelper.setActivity(this);
	}
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int DELETE_ALL_ID = Menu.FIRST + 2;
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		
		
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
		menu.add(1, DELETE_ALL_ID, 0, R.string.menu_delete_all);
		
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
        case DELETE_ID:
            AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
            destCursor.moveToFirst();
    		destCursor.moveToPosition(info.position);
    		
            mDbHelper.deleteDestination(destCursor.getInt(destCursor.getColumnIndexOrThrow(DataBaseAdapterForDestHistory.KEY_ROWID)));
            fillDestinationHistoryData();
            return true;
        case DELETE_ALL_ID:
        	mDbHelper.deleteAllDestination();
        	fillDestinationHistoryData();
            return true;
		}
		// TODO Auto-generated method stub
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == ACTIVITY_POI_DETAIL) {
            if (resultCode == RESULT_OK) {
                
            	int[] list = new int[2];
				list[0] = ((TBTNaviDemoRouteData)getApplication()).destPoint.getLatitudeE6();
				list[1] = ((TBTNaviDemoRouteData)getApplication()).destPoint.getLongitudeE6();
            	String title = ((TBTNaviDemoRouteData)getApplication()).destName;
				Cursor cursor = mDbHelper.fetchDestinationByTitleAndLocation(title, list[1], list[0]);
				if (cursor.getCount() == 0) {
					mDbHelper.addDestination(title, 
					list[1], list[0], System.currentTimeMillis());
				}
				else
				{
					mDbHelper.updateDestination(cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseAdapterForDestHistory.KEY_ROWID)), 
							title, list[1], list[0], System.currentTimeMillis());
				}
				cursor.close();
				destCursor.close();
				mDbHelper.close();
				
            	Intent intent = new Intent(getApplicationContext(), TBTNaviDemoMapView.class);
				Bundle bundle=new Bundle();
				bundle.putIntArray("DESTPOINT", list);
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
        	    finish();
            }
        }
	}

	private static final int ACTIVITY_POI_DETAIL = 3;
	
	private OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == btnSearch)
			{
				tvTitleHistory.setBackgroundResource(R.drawable.layout_profile_title_destination);
				 
				if (edtPoi.length() > 0){
					
					InputMethodManager imm = (InputMethodManager)v
						.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);   
					if(imm.isActive()){
						imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0 );   
					}
					
					sPoiInputKeyWord = edtPoi.getText().toString();
					lvPoi.setVisibility(View.INVISIBLE);
					
					doSearchQuery(sPoiInputKeyWord);
					
					mDataFromHistory = false;
								
 
				}else{                                                            
					Toast.makeText(TBTNaviDemoSearch.this, "��ѯ���ݲ���Ϊ��!",           
							Toast.LENGTH_LONG).show();                                  
				} 
//				finish();
			}
			else if (v == btnBack)
			{
				destCursor.close();
				destCursor = null;
				mDbHelper.close();
				
				Intent intent = new Intent(getApplicationContext(), TBTNaviDemoMapView.class);
				setResult(RESULT_CANCELED, intent);
				finish();
			}
			else
			{
				
			}
		}
	};
	
	private PoiPagedResult result;
	
	private void doSearchQuery(final String keyWord) {
		
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage("��������:\n" + keyWord);
		progDialog.show();

		Timer timer = new Timer();
        TimerTask task = new TimerTask() {
         @Override
	         public void run() {
        	 	
        	 	try {
        			PoiSearch poiSearch = new PoiSearch(TBTNaviDemoSearch.this, "c2b0f58a6f09cafd1503c06ef08ac7aeb7ddb91a988ffd75a4c867cbae60eab95c6e7d6dca4eb197", new PoiSearch.Query(keyWord, PoiTypeDef.All, "010")); // ���������ַ�����"010Ϊ��������"
        			result = poiSearch.searchPOI();

        		} catch (AMapException e) {
        			progDialog.dismiss();
        			handler.sendMessage(Message.obtain(handler, 30002));
        			e.printStackTrace();
        		}
        	 	
        	 	if(progDialog.isShowing()){
        			if (result != null) {
        				handler.sendMessage(Message
        						.obtain(handler, 30001));
        			} else {
        				handler.sendMessage(Message.obtain(handler, 30003));
        			}
        		}
	         }
         
        };
    
        timer.schedule(task, 10);
		
		

	}	
	
	 private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 30001) {
				progDialog.dismiss();				
				DrawList(1);
			}else if (msg.what == 30002) {
				progDialog.dismiss();
				Toast.makeText(getApplicationContext(), "����ʧ��,�����������ӣ�",
					Toast.LENGTH_LONG).show();
			}else if (msg.what == 30003) {
				progDialog.dismiss();
				Toast.makeText(getApplicationContext(), "����ʧ��,�����������ݣ�",
					Toast.LENGTH_LONG).show();
			}
		}
    };

    public void DrawList(int nPage){						
	
		try{ 
			list = result.getPage(nPage);
			if (list == null)
			{
				Toast.makeText(getApplicationContext(), "����ʧ�ܣ������������ݣ�",     
						Toast.LENGTH_LONG).show();
				return;
			}
			
			int i = list.size();
			
			String[] sw = new String[i];

			for (int loop = 0; loop < i; ++loop){
				PoiItem poi = list.get(loop);						
				
				sw[loop] = poi.toString();								
			}
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					R.layout.search_list_item,
					sw);
			
			lvPoi.setAdapter(adapter);  
			lvPoi.setVisibility(View.VISIBLE);
		}
		catch (AMapException e)
		{
			Toast.makeText(getApplicationContext(), "����ʧ�ܣ������������ݣ�",     
					Toast.LENGTH_LONG).show();    
		}
	}
    
    static private Cursor destCursor = null;
    private void fillDestinationHistoryData()
    {
    	destCursor = mDbHelper.fetchAllDestination();
    	startManagingCursor(destCursor);
    	
    	String[] from = new String[]{DataBaseAdapterForDestHistory.KEY_TITLE};
    	int[] to = new int[]{R.id.text1};
    	
    	SimpleCursorAdapter destHistory = 
                new SimpleCursorAdapter(this, R.layout.search_list_item, destCursor, from, to);
    	
    	lvPoi.setAdapter(destHistory);
    }
}
