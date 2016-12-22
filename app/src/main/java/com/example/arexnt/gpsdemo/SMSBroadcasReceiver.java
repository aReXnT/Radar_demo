package com.example.arexnt.gpsdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;


//接收并解析短信
public class SMSBroadcasReceiver extends BroadcastReceiver {
    private String TAG = "SMSReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");   //接收数据
        if(pdus != null) {
            for (Object p : pdus) {
                byte[] pdu = (byte[]) p;
                SmsMessage message = SmsMessage.createFromPdu(pdu);
                String body = message.getMessageBody();             //发送内容
                String sender = message.getOriginatingAddress();    //短信发送方
                //            Log.i("SMScontent",sender + ',' + date + ',' + body);
                String smsText = "where are you?";
                if (body.equals(smsText)) {
                    Log.i(TAG, "getSMS from:" + sender + ",sending my location.");
                    Intent sendIntent = new Intent(context, SendLocateService.class);
                    sendIntent.putExtra("phoneNumber", sender);
                    sendIntent.setAction("com.arexnt.sendLocateService");
                    context.startService(sendIntent);
                    abortBroadcast();   //中断广播
                } else if (body.matches("^(\\d*\\.)?\\d+\\/(\\d*\\.)?\\d+$")) {
                    Log.i(TAG, "getSMS from:" + sender + ", add location into database.");
                    Intent setLocInent = new Intent(context, SetLocateService.class);
                    setLocInent.putExtra("phoneNumber", sender);
                    setLocInent.putExtra("body", body);
                    setLocInent.setAction("com.arexnt.setLocateService");
                    context.startService(setLocInent);
                    abortBroadcast();   //中断广播
                }
            }
        }
    }

}
