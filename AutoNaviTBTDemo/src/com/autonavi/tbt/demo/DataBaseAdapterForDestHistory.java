package com.autonavi.tbt.demo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseAdapterForDestHistory {

    public static final String KEY_TITLE = "title";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_DATETIME = "datetime";
    public static final String KEY_ROWID = "_id";
    
    
    private static final String TAG = "DestHistoryDbAdapter";
    
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    private static final String DATABASE_CREATE =
            "create table desthistory (_id integer primary key autoincrement, "
            + "title text not null, longitude integer not null, latitude integer not null, datetime integer not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "desthistory";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }
    
    public DataBaseAdapterForDestHistory(Context ctx) {
        this.mCtx = ctx;
    }
    
    public DataBaseAdapterForDestHistory open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }
    
    // add destination
    public long addDestination(String title, int longitude, int latitude, long datetimeupdate) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_LONGITUDE, longitude);
        initialValues.put(KEY_LATITUDE, latitude);
        initialValues.put(KEY_DATETIME, datetimeupdate);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    // delete destination
    public boolean deleteDestination(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public boolean deleteAllDestination()
    {
    	mDb.delete(DATABASE_TABLE, KEY_ROWID + ">0 ", null);
    	//mDb.execSQL("delete from desthistory where _id like *; ");
    	return true;
    }
    
    public Cursor fetchAllDestination() throws SQLException{

        Cursor mCursor = mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
        		KEY_LONGITUDE, KEY_LATITUDE, KEY_DATETIME}, null, null, null, null, /*null*/KEY_DATETIME +" desc" );
        
        if (mCursor != null) {
        	mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public Cursor fetchDestination(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_TITLE, KEY_LONGITUDE, KEY_LATITUDE, KEY_DATETIME}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    public Cursor fetchDestinationByTitleAndLocation(String title, int longitude, int latitude) throws SQLException {
    	Cursor mCursor = 
    		mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_TITLE, KEY_LONGITUDE, KEY_LATITUDE, KEY_DATETIME}, 
                    KEY_TITLE + "=?"  /*+ " and " + 
                    KEY_LONGITUDE + "=" + longitude + " and " +
                    KEY_LATITUDE + "=" + latitude*/, new String[] {title}, null, null, null, null);
    	
    	if (mCursor != null) {
    		mCursor.moveToFirst();
    	}
    	return mCursor;
    }
    
    public boolean updateDestination(long rowId, String title, int longitude, int latitude, long datetimeupdate) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_LONGITUDE, longitude);
        args.put(KEY_LATITUDE, latitude);
        args.put(KEY_DATETIME, datetimeupdate);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
