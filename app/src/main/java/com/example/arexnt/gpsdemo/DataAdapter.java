package com.example.arexnt.gpsdemo;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by arexnt on 2016/11/19.
 */

public class DataAdapter extends BaseAdapter {
    private Context mContext;
    private Cursor mCursor;
    private Boolean friendly;

    public DataAdapter(Context context, Cursor cursor, Boolean friendly){
        this.mCursor = cursor;
        this.mContext = context;
        this.friendly = friendly;

    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return mCursor.getPosition();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, null);
        if (friendly)
            convertView.findViewById(R.id.list_item_background)
                    .setBackgroundResource(R.drawable.friends_list_item_background);
        else
            convertView.findViewById(R.id.list_item_background)
                    .setBackgroundResource(R.drawable.enemies_list_item_background);

        mCursor.moveToPosition(position);
        TextView TVnumber = (TextView) convertView.findViewById(R.id.phoneNumber);
        String number = mCursor.getString(mCursor.getColumnIndex(DataDB.NUMBER));
        TVnumber.setText(number);
        return convertView;
    }


    //这个版本是用序列化保存数据到数据库
//    public View getView(int position, View convertView, ViewGroup parent) {
//        convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, null);
//        convertView.findViewById(R.id.list_item_background)
//                .setBackgroundResource(R.drawable.enemies_list_item_background);
//        TextView TVnumber = (TextView) convertView.findViewById(R.id.phoneNumber);
//        mCursor.moveToPosition(position);
//
//        //反序列化
//        Data mData = new Data();
//        try {
//            if(friendly){
//                byte data[] = mCursor.getBlob(mCursor.getColumnIndex("friend"));
//                ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
//                ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
//                mData = (Data) inputStream.readObject();
//                inputStream.close();
//                arrayInputStream.close();
//                Log.i("DBdata",mData.getNUMBER());
//                convertView.findViewById(R.id.list_item_background)
//                        .setBackgroundResource(R.drawable.friends_list_item_background);
//                TVnumber.setText(mData.getNUMBER());
//
//            }else {
//                byte data[]= mCursor.getBlob(mCursor.getColumnIndex("enemy"));
//                ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
//                ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
//                mData = (Data) inputStream.readObject();
//                inputStream.close();
//                arrayInputStream.close();
//                Log.i("DBdata",mData.getNUMBER());
//                convertView.findViewById(R.id.list_item_background)
//                        .setBackgroundResource(R.drawable.enemies_list_item_background);
//                TVnumber.setText(mData.getNUMBER());
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return convertView;
//
//    }

}
