package com.example.arexnt.gpsdemo;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FriendItemDetail extends AppCompatActivity implements View.OnClickListener{
    private EditText number;
    private TextView latlng;
    private TextView addr;
    private DataDB mDB;
    private SQLiteDatabase mDatabase;
    private Button del;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_item_detail);
        number = (EditText) findViewById(R.id.friend_item_detail_number);
        latlng = (TextView) findViewById(R.id.friend_item_detail_LatLng);
        addr = (TextView) findViewById(R.id.friend_item_detail_nearestAddr);
        del = (Button) findViewById(R.id.btn_friend_item_del);
        save = (Button) findViewById(R.id.btn_friend_item_save);
        number.setText(getIntent().getStringExtra(DataDB.NUMBER));
        String StrLatlng = Double.toString(getIntent().getDoubleExtra(DataDB.LATITUDE,0))
                + "," + Double.toString(getIntent().getDoubleExtra(DataDB.LONGITUDE,0));
        latlng.setText(StrLatlng);
        addr.setText(getIntent().getStringExtra(DataDB.ADDRS));
        mDB = new DataDB(this);
        mDatabase = mDB.getWritableDatabase();
        del.setOnClickListener(this);
        save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_friend_item_del:
                AlertDialog.Builder dialog = new AlertDialog.Builder(FriendItemDetail.this);
                dialog.setTitle(R.string.app_name);
                dialog.setMessage("确认删除？");
                dialog.setCancelable(true);
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabase.delete(DataDB.TABLE_NAME, "_id=" + getIntent().getIntExtra(DataDB.ID,0),null);
                        finish();
                    }
                });
                dialog.show();

                break;
            case R.id.btn_friend_item_save:
                ContentValues cv = new ContentValues();
                cv.put(DataDB.NUMBER,number.getText().toString());
                mDatabase.update(DataDB.TABLE_NAME, cv, "_id=" + getIntent().getIntExtra(DataDB.ID, 0), null);
                finish();
                break;
        }
    }
}
