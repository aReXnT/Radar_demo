package com.example.arexnt.gpsdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class EnemyList extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enemy_list);
        Button btnRadar = (Button) findViewById(R.id.btn_enemyListToRadar);
        Button btnFriend = (Button) findViewById(R.id.btn_enemyListToFriend);
        Button btnAdd = (Button) findViewById(R.id.btn_enemyList_add);
        btnRadar.setOnClickListener(this);
        btnFriend.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_enemyListToRadar:
                onBackPressed();
                break;
            case R.id.btn_enemyListToFriend:
                Intent friend = new Intent("start.FriendList");
                startActivity(friend);
                finish();
                break;
            case R.id.btn_enemyList_add:
                Intent dialog = new Intent("start.dialogButton");
                startActivity(dialog);
                break;
        }
    }
}
