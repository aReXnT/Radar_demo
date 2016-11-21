package com.example.arexnt.gpsdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class dialogButton extends Activity implements View.OnClickListener{
    private Intent it;


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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_sendSMS:
                break;
            case R.id.btn_dialogMap:
                Intent dialogMap = new Intent("start.dialogMapView");
                dialogMap.putExtra("friendly",it.getBooleanExtra("friendly",true));
                startActivity(dialogMap);
                finish();
                break;
        }
    }
}
