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
    public static final String CONTENT = "content";
    public static final String NUMBER = "number";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String ALTITUDE = "altitude";
    public static final String ACCURACY = "accuracy";
    public static final String ADDRS = "addrs";
    public static final String Friendly = "friendly";

    public DataDB(Context context){
        super(context,"radar", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("create table if not exists data(_id integer primary key autoincrement,friend text,enemy text)");
        db.execSQL("CREATE TABLE "
                + TABLE_NAME + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NUMBER + " TEXT, "
                + LATITUDE + " REAL, "
                + LONGITUDE + " REAL, "
                + ALTITUDE + " TEXT, "
                + ACCURACY + " TEXT, "
                + ADDRS + " TEXT, "
                + Friendly + " TEXT)"
        );
//        db.execSQL("CREATE TABLE "
//                + TABLE_NAME + "("
//                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + CONTENT + " TEXT)"
//        );
//        db.execSQL("drop table if exists data");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("drop table if exists data");
    }
}
