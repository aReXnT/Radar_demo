package com.example.arexnt.gpsdemo;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class dialogButton extends Activity implements View.OnClickListener{
    private Intent it;
    private DataDB mDB;
    private SQLiteDatabase mDatabase;
    private String strPhoneNumber;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        it = getIntent();

        setContentView(R.layout.activity_dialog_button);

        Button sendSMS = (Button) findViewById(R.id.btn_sendSMS);
        Button dialogMap = (Button) findViewById(R.id.btn_dialogMap);
        sendSMS.setOnClickListener(this);
        dialogMap.setOnClickListener(this);
        mDB = new DataDB(this);
        mDatabase = mDB.getWritableDatabase();
        SharedPreferences.Editor editor = getSharedPreferences("location",MODE_PRIVATE).edit();
        editor.putString("number", strPhoneNumber);
        if(it.getBooleanExtra("friendly",true)){
            editor.putBoolean("friendly",true);
        }else {
            editor.putBoolean("friendly",false);
        }
        editor.commit();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_sendSMS:

                final EditText edt = new EditText(this);
                edt.setInputType(InputType.TYPE_CLASS_PHONE);
                edt.setMaxLines(1);
                InputFilter[] filters = {new InputFilter.LengthFilter(11)};
                edt.setFilters(filters);

                AlertDialog.Builder sms = new AlertDialog.Builder(this);
                sms.setTitle("电话号码");
                sms.setIcon(android.R.drawable.ic_menu_call);
                sms.setView(edt);
                sms.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        strPhoneNumber = edt.getText().toString();
//                        Toast.makeText(dialogButton.this, "号码是：" + str,
//                                Toast.LENGTH_SHORT).show();

                        if(!isExist()) {
                            sendSMS(strPhoneNumber);
                        }else {
                            String relation  ;
                            String friendly;
                            String ToastStr;
                            while(mCursor.moveToNext()){
                                relation = mCursor.getString(mCursor.getColumnIndex("friendly"));
                                if(relation.equals("0")){
                                    friendly = "敌人";
                                }else {
                                    friendly = "好友";
                                }
                                Log.i("relation",relation);
                                ToastStr = strPhoneNumber + "已经存在," + "他是" + friendly + "!";
                                Toast.makeText(getApplicationContext(), ToastStr,Toast.LENGTH_SHORT).show();
                            }


                        }

                    }
                });
                sms.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                sms.show();


                break;
            case R.id.btn_dialogMap:
                Intent dialogMap = new Intent("start.dialogMapView");
                dialogMap.putExtra("friendly",it.getBooleanExtra("friendly",true));
                startActivity(dialogMap);
                finish();
                break;
        }
    }

    private void sendSMS(String number){
        SmsManager smsManager = SmsManager.getDefault();
        //发送广播，监听是否发送和接收成功
        PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SEND"), 0);
        PendingIntent deliveryIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
        smsManager.sendTextMessage(number, null, "where are you?", sentIntent, deliveryIntent);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    public boolean isExist(){
        boolean flag = false;
        mCursor = mDatabase.rawQuery("select * from data where number=?", new String[]{strPhoneNumber});
        if(mCursor.getCount()==0)
            flag = false;
        else
            flag = true;
        return flag;
    }
}
