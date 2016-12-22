package com.example.arexnt.gpsdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class FriendList extends AppCompatActivity implements View.OnClickListener {
    private ListView listView;
    private Cursor cursor;
    private DataDB mDB;
    private SQLiteDatabase mDatabase;

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
        listView = (ListView) findViewById(R.id.friendListView);
        listView.setOnItemClickListener(mOnItemClickListener);
        listView.setOnItemLongClickListener(mOnItemLongClickListener);
        mDB = new DataDB(this);
        mDatabase = mDB.getWritableDatabase();
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
                Intent dialog = new Intent("start.dialogButton");
                dialog.putExtra("friendly",true);
                startActivity(dialog);
                break;
        }
    }

    public void selectDB(){
        cursor = mDatabase.rawQuery("select * from data where friendly=?", new String[]{"1"});
//        cursor = mDatabase.query(DataDB.TABLE_NAME, null, "friendly",new String[]{"1"},null,null,null);
        Log.i("readDB",Integer.toString(cursor.getCount()));
        DataAdapter adapter = new DataAdapter(this,cursor,true);
        listView.setAdapter(adapter);

    }


    @Override
    protected void onResume() {
        super.onResume();
        selectDB();
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            cursor.moveToPosition(position);
            Intent i = new Intent(FriendList.this, FriendItemDetail.class);
            i.putExtra(DataDB.ID, cursor.getInt(cursor.getColumnIndex(DataDB.ID)));
            i.putExtra(DataDB.NUMBER, cursor.getString(cursor.getColumnIndex(DataDB.NUMBER)));
            i.putExtra(DataDB.LATITUDE, cursor.getDouble(cursor.getColumnIndex(DataDB.LATITUDE)));
            i.putExtra(DataDB.LONGITUDE, cursor.getDouble(cursor.getColumnIndex(DataDB.LONGITUDE)));
            i.putExtra(DataDB.ADDRS,cursor.getString(cursor.getColumnIndex(DataDB.ADDRS)));
            i.putExtra("friendly",true);
            startActivity(i);

        }
    };

    private AdapterView.OnItemLongClickListener mOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            cursor.moveToPosition(position);
            AlertDialog.Builder dialog = new AlertDialog.Builder(FriendList.this);
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
                    mDatabase.delete(DataDB.TABLE_NAME, "_id=" + cursor.getInt(cursor.getColumnIndex(DataDB.ID)), null);
                    onResume();
                }
            });
            dialog.show();
            return true;
        }
    };
}
