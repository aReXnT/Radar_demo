package com.example.arexnt.gpsdemo;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by arexnt on 2016/12/21.
 */

public class SetLocateService extends Service{
    private String phoneNumber;
    private String body;
    private double lat;
    private double lon;
    private DataDB mDB;
    private SQLiteDatabase mDatabase;
    private Cursor mCursor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDB = new DataDB(this);
        mDatabase = mDB.getWritableDatabase();
        phoneNumber = intent.getStringExtra("phoneNumber");
        body = intent.getStringExtra("body");
        String[] Latlng = body.split("/");
        lat = Double.parseDouble(Latlng[0]);
        lon = Double.parseDouble(Latlng[1]);
        ContentValues cv = new ContentValues();

        try {
            mCursor = mDatabase.rawQuery("select * from data where number=?", new String[]{phoneNumber});
            SharedPreferences preferences = getSharedPreferences("location",MODE_PRIVATE);
            boolean relation = preferences.getBoolean("friendly",false);
            Log.i("setloc",Boolean.toString(relation));
            if (mCursor.getCount()!=0){
                cv.put(DataDB.NUMBER,phoneNumber);
                cv.put(DataDB.LATITUDE,lat);
                cv.put(DataDB.LONGITUDE,lon);
                mDatabase.update(DataDB.TABLE_NAME,cv,"number=?",new String[]{phoneNumber});
            }else {
                cv.put(DataDB.NUMBER,phoneNumber);
                cv.put(DataDB.LATITUDE,lat);
                cv.put(DataDB.LONGITUDE,lon);
                if (relation) {
                    cv.put(DataDB.Friendly, 1);
                }else{
                    cv.put(DataDB.Friendly, 0);
                }
                mDatabase.insert(DataDB.TABLE_NAME,null,cv);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }
}
