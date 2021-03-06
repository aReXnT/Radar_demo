package com.example.arexnt.gpsdemo;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity    {
    MapView mMapView;
    BaiduMap mBaiduMap;
    public LocationClient mLocationClient = null;
    BitmapDescriptor mCurrentMarker;
    private Button btnLocate;
    private Button btnRefesh;
    private SMSBroadcasReceiver mReceiver;
    private IntentFilter recevieFilter;
    public MyLocationListenner myListener = new MyLocationListenner();
    boolean isFirstLoc = true;// 是否首次定位

    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //logcat 记录调试信息
//            locateDebug(location);
            SharedPreferences.Editor editor = getSharedPreferences("location",MODE_PRIVATE).edit();
            editor.putString("lat",Double.toString(location.getLatitude()));
            editor.putString("lon",Double.toString(location.getLongitude()));
            editor.commit();
            //map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);    //设置定位数据

            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 18);   //设置地图中心点以及缩放级别
                mBaiduMap.animateMapStatus(u);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        //开启定位图层
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //定位初始化
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        this.initLocation();
        mLocationClient.start();

        View.OnClickListener btnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btn_refresh:
                        mBaiduMap.clear();
                        btnRefesh = (Button) findViewById(R.id.btn_refresh);
                        final ImageView scanCover = (ImageView) findViewById(R.id.scanCover);
                        final ImageView radarSweep = (ImageView) findViewById(R.id.radarSweep);
                        radarSweep.setVisibility(View.VISIBLE);
                        scanCover.setVisibility(View.VISIBLE);
                        scanCover.setAlpha(.2f);
                        btnRefesh.setBackgroundResource(R.drawable.button_refresh_on);
                        RotateAnimation refreshAnim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
                        refreshAnim.setDuration(2000);
                        refreshAnim.setInterpolator(new FastOutLinearInInterpolator());
                        refreshAnim.setRepeatCount(1);
                        refreshAnim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                RotateAnimation sweepAnim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 1f);
                                sweepAnim.setDuration(2000);
                                sweepAnim.setInterpolator(new FastOutLinearInInterpolator());
                                sweepAnim.setRepeatCount(1);
                                radarSweep.startAnimation(sweepAnim);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                btnRefesh.setBackgroundResource(R.drawable.button_refresh);
                                scanCover.setVisibility(View.INVISIBLE);
                                radarSweep.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        v.startAnimation(refreshAnim);
                        sendSMS();
                        //添加maker

                        readDB();
                        break;

                    case R.id.btn_locate:
                        btnLocate = (Button)findViewById(R.id.btn_locate);
                        btnLocate.setBackgroundResource(R.drawable.button_locate_on);

                        //重新定位
                        LatLng ll = new LatLng(mLocationClient.getLastKnownLocation().getLatitude(),
                                mLocationClient.getLastKnownLocation().getLongitude());
                        double zoomLevel = mBaiduMap.getMapStatus().zoom;
                        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,(float)zoomLevel);   //设置地图中心点以及缩放级别
                        mBaiduMap.animateMapStatus(u);

                        final ImageView locateCover = (ImageView)findViewById(R.id.locateCover);
                        final Animation locateAnim = new AlphaAnimation(1f,.4f);
                        locateAnim.setDuration(1000);
                        locateAnim.setRepeatCount(2);
                        btnLocate.setAnimation(locateAnim);

                        locateAnim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                                locateCover.setVisibility(View.VISIBLE);
                                locateCover.setAnimation(locateAnim);
                                locateCover.setAnimation(locateAnim);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                btnLocate.setBackgroundResource(R.drawable.button_locate);
                                locateCover.setVisibility(View.INVISIBLE);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        v.startAnimation(locateAnim);

                        break;

                    case R.id.btn_friendList:
                        Intent friendListIntent = new Intent("start.FriendList");
                        startActivity(friendListIntent);
                        break;
                    case R.id.btn_enemyList:
                        Intent enemyListIntent = new Intent("start.EnemyList");
                        startActivity(enemyListIntent);
                        break;

                }



            }
        };
        Button btnFriendList = (Button) findViewById(R.id.btn_friendList);
        Button btnEnemyList = (Button) findViewById(R.id.btn_enemyList);
        btnLocate = (Button) findViewById(R.id.btn_locate);
        btnRefesh = (Button) findViewById(R.id.btn_refresh);
        btnLocate.setOnClickListener(btnClickListener);
        btnRefesh.setOnClickListener(btnClickListener);
        btnEnemyList.setOnClickListener(btnClickListener);
        btnFriendList.setOnClickListener(btnClickListener);
        recevieFilter = new IntentFilter();
        recevieFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        mReceiver = new SMSBroadcasReceiver();
        registerReceiver(mReceiver,recevieFilter);

    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onRestart();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开GPS
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true); // 返回的定位结果包含手机机头的方向
        mLocationClient.setLocOption(option);
    }

    private void locateDebug(BDLocation location){
        StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        sb.append(location.getTime());
        sb.append("\nerror code : ");
        sb.append(location.getLocType());
        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());
        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());
        sb.append("\nradius : ");
        sb.append(location.getRadius());
        if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());// 单位：公里每小时
            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());
            sb.append("\nheight : ");
            sb.append(location.getAltitude());// 单位：米
            sb.append("\ndirection : ");
            sb.append(location.getDirection());// 单位度
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            sb.append("\ndescribe : ");
            sb.append("gps定位成功");

        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            //运营商信息
            sb.append("\noperationers : ");
            sb.append(location.getOperators());
            sb.append("\ndescribe : ");
            sb.append("网络定位成功");
        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
            sb.append("\ndescribe : ");
            sb.append("离线定位成功，离线定位结果也是有效的");
        } else if (location.getLocType() == BDLocation.TypeServerError) {
            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");
        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        }
        sb.append("\nlocationdescribe : ");
        sb.append(location.getLocationDescribe());// 位置语义化信息
        List<Poi> list = location.getPoiList();// POI数据
        if (list != null) {
            sb.append("\npoilist size = : ");
            sb.append(list.size());
            for (Poi p : list) {
                sb.append("\npoi= : ");
                sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
            }
        }
        Log.i("BaiduLocationApiDem", sb.toString());
    }

    private void sendSMS(){
        DataDB mDB = new DataDB(this);
        SQLiteDatabase mDatabase = mDB.getWritableDatabase();
        Cursor cursor = mDatabase.rawQuery("select number from data",null);
        int i = cursor.getCount();
        Log.i("testsendSMS",Integer.toString(i));
        while(cursor.moveToNext()){
            String number = cursor.getString(cursor.getColumnIndex(DataDB.NUMBER));
            SmsManager manager = SmsManager.getDefault();
            String sendText = "where are you?";
            ArrayList<String> list = manager.divideMessage(sendText);
            for (String text : list) {
                manager.sendTextMessage(number, null, text, null, null);
            }
        }
        try {
            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private void readDB(){

        DataDB mDB = new DataDB(this);
        SQLiteDatabase mDatabase = mDB.getWritableDatabase();
        Cursor cursor = mDatabase.query(DataDB.TABLE_NAME,null,null,null,null,null,null);
        Log.i("selectDB",Integer.toString(cursor.getCount()));
        while (cursor.moveToNext()){
            Data mData = new Data();
            mData.setNUMBER(cursor.getString(cursor.getColumnIndex(DataDB.NUMBER)));
            mData.setLatitude(cursor.getDouble(cursor.getColumnIndex(DataDB.LATITUDE)));
            mData.setLongitude(cursor.getDouble(cursor.getColumnIndex(DataDB.LONGITUDE)));
            int friendly = cursor.getInt(cursor.getColumnIndex(DataDB.Friendly));
            Log.i("getFriendly",Integer.toString(friendly));
            if (friendly == 1)
                mData.setFriendly(true);
            else
                mData.setFriendly(false);
            addMaker(mData);
        }

    }

    private void addMaker(Data mData){
        BitmapDescriptor bitmap;
        if(mData.getFriendly())
            bitmap = BitmapDescriptorFactory.fromResource(R.drawable.friend_marker);
        else
            bitmap = BitmapDescriptorFactory.fromResource(R.drawable.enemy_marker);
        LatLng latLng = new LatLng(mData.getLatitude(),mData.getLongitude());
        OverlayOptions option = new MarkerOptions().position(latLng).icon(bitmap);
        mBaiduMap.addOverlay(option);
        Log.i("dbLocation",latLng.toString());
    }

}
