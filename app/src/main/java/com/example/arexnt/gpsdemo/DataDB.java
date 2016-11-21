package com.example.arexnt.gpsdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by arexnt on 2016/11/15.
 */

public class DataDB extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "data";
    public static final String ID = "_id";
    public static final String NUMBER = "number";
    public static final String LatLng = "latlng";
    public static final String ALTITUDE = "altitude";
    public static final String ACCURACY = "accuracy";
    public static final String ADDRS = "addrs";
    public static final String Friendly = "friendly";

    public DataDB(Context context){
        super(context,"data", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "
                + TABLE_NAME + "("
                + ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NUMBER + " TEXT, "
                + LatLng + " TEXT, "
                + ALTITUDE + " TEXT, "
                + ACCURACY + " TEXT, "
                + ADDRS + " TEXT, "
                + Friendly + " BLOB)"
        );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
