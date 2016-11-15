package com.example.arexnt.gpsdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FriendList extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        Button btnRadar = (Button) findViewById(R.id.btn_friendListToRadar);
        Button btnEnemy = (Button) findViewById(R.id.btn_friendListToEnemy);
        Button btnAdd = (Button) findViewById(R.id.btn_friendList_add);
        btnRadar.setOnClickListener(this);
        btnEnemy.setOnClickListener(this);
        btnAdd.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_friendListToRadar:
                onBackPressed();

                break;
            case R.id.btn_friendListToEnemy:
                Intent friend = new Intent("start.EnemyList");
                startActivity(friend);
                finish();
                break;
            case R.id.btn_friendList_add:
                Intent dialog = new Intent("start.dialogMapView");
                startActivity(dialog);
                break;
        }
    }

}
