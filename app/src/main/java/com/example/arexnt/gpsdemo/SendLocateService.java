package com.example.arexnt.gpsdemo;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by arexnt on 2016/12/21.
 */

public class SendLocateService extends Service{
    private String phoneNumber = null;
    private String lat;
    private String lon;
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
        phoneNumber = intent.getStringExtra("phoneNumber");
        SharedPreferences preferences = getSharedPreferences("location",MODE_PRIVATE);
        lat = preferences.getString("lat","");
        lon = preferences.getString("lon","");
        Log.i("sendSMS",lat + "/" + lon  );

        String sendText = lat + "/" + lon;
        SmsManager manager = SmsManager.getDefault();
        ArrayList<String> list = manager.divideMessage(sendText);
        for (String text : list) {
            manager.sendTextMessage(phoneNumber, null, text, null, null);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
