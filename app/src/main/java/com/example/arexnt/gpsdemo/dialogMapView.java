package com.example.arexnt.gpsdemo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class dialogMapView extends Activity implements View.OnClickListener {
    MapView mMapView;
    BaiduMap mBaiduMap;
    private Intent it;
    private Boolean relation;
    private Button btn_dialog_done;
    private Button btn_dialog_locate;
    public LocationClient mLocationClient = null;
    public MyLocationListenner myListener = new MyLocationListenner();
    boolean isFirstLoc = true;// 是否首次定位
    private Animation centerAnimation;
    private ImageView dialogCenterImage;
    private LatLng mFinalChoosePosition;
    private GeoCoder mSearch = null;
    private EditText phoneNumber;
    private EditText textLat;
    private EditText textLng;
    private String strPhoneNumber;
    private String strAddrs;
    private DataDB mDB;
    private SQLiteDatabase mDatabase;
    public MyGeoCoder mGeoCoder = new MyGeoCoder();

    public class MyGeoCoder implements OnGetGeoCoderResultListener {
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(dialogMapView.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                        .show();
                Log.i("RealAddrs","error");
                return;
            }
//
            Log.i("RealAddrs", result.getAddress());
            EditText addrs = (EditText) findViewById(R.id.input_ADDRS);
            addrs.setEnabled(false);
            strAddrs = result.getAddress();
            addrs.setText(strAddrs);

        }

        @Override
        public void onGetGeoCodeResult(GeoCodeResult result) {

        }
    }

    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_dialog_map_view);

        //初始化数据库
        mDB = new DataDB(this);
        mDatabase = mDB.getWritableDatabase();

        //
        it = getIntent();
        //设置按钮监听
        btn_dialog_done = (Button) findViewById(R.id.dialog_done);
        btn_dialog_locate = (Button) findViewById(R.id.dialog_locate);
        btn_dialog_done.setOnClickListener(this);
        btn_dialog_locate.setOnClickListener(this);


        //开启定位图层
        mMapView = (MapView) findViewById(R.id.dialog_bmapView);
        mBaiduMap = mMapView.getMap();
        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //定位初始化
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        this.initLocation();
        mLocationClient.start();

        //监听反地理编码
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(mGeoCoder);

        //监听地图移动,获取中心位置
        dialogCenterImage = (ImageView) findViewById(R.id.dialog_center_image);
        relation = it.getBooleanExtra("friendly", true);
        if (relation)
            dialogCenterImage.setImageResource(R.drawable.friend_marker);
        else
            dialogCenterImage.setImageResource(R.drawable.enemy_marker);

        centerAnimation = AnimationUtils.loadAnimation(this, R.anim.center_anim);//钉子动画
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus status) {

            }

            @Override
            public void onMapStatusChange(MapStatus status) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus status) {
                dialogCenterImage.startAnimation(centerAnimation);
                mFinalChoosePosition = status.target;
                String locationStr = mFinalChoosePosition.toString();
                Log.i("centerLocation", locationStr);
                Log.i("centerLocation", status.toString());

//                Toast.makeText(dialogMapView.this,locationStr,Toast.LENGTH_SHORT).show();
                mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                        .location(mFinalChoosePosition));

                phoneNumber = (EditText) findViewById(R.id.edit_phoneNumber);
                textLat = (EditText) findViewById(R.id.input_Lat);
                textLng = (EditText) findViewById(R.id.input_Lng);


                textLat.setEnabled(false);
                textLat.setText(Double.valueOf(mFinalChoosePosition.latitude).toString());
                textLng.setEnabled(false);
                textLng.setText(Double.valueOf(mFinalChoosePosition.longitude).toString());


            }
        });


    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开GPS
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true); // 返回的定位结果包含手机机头的方向
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_done:
                phoneNumber = (EditText) findViewById(R.id.edit_phoneNumber);
                strPhoneNumber = phoneNumber.getText().toString();
                addDB();

                finish();
                break;
            case R.id.dialog_locate:
                btn_dialog_locate.setBackgroundResource(R.drawable.button_dialog_locating);
                AlphaAnimation btn_flicker = new AlphaAnimation(0.4f, 1f);
                btn_flicker.setDuration(750);
                btn_flicker.setRepeatCount(3);
                btn_flicker.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        btn_dialog_locate.setBackgroundResource(R.drawable.button_dialog_location);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                btn_dialog_locate.startAnimation(btn_flicker);

                LatLng ll = new LatLng(mLocationClient.getLastKnownLocation().getLatitude(),
                        mLocationClient.getLastKnownLocation().getLongitude());
                double zoomLevel = mBaiduMap.getMapStatus().zoom;
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, (float) zoomLevel);
                mBaiduMap.animateMapStatus(u);

                break;
        }
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        mSearch.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

//    public void addDB() {
//        Data mData = new Data();
//        mData.setNUMBER(strPhoneNumber);
//        mData.setLatitude(mFinalChoosePosition.latitude);
//        mData.setLongitude(mFinalChoosePosition.longitude);
//        mData.setADDRS(strAddrs);
//        boolean relation = it.getBooleanExtra("friendly", true);
//        mData.setFriendly(relation);

//        //序列化数据
//        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
//        try {
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
//            objectOutputStream.writeObject(mData);
//            objectOutputStream.flush();
//            byte data[] = arrayOutputStream.toByteArray();
//            objectOutputStream.close();
//            arrayOutputStream.close();
//            if(relation){
//                mDatabase.execSQL("insert into data(friend) values(?)", new Object[]{data});
//                Log.i("insertData","insert a friend");
//
//            }else{
//                mDatabase.execSQL("insert into data(enemy) values(?)", new Object[]{data});
//                Log.i("insertData","insert a enemy");
//            }
//            mDatabase.close();
//            Log.i("insert",data.toString());
//        } catch (Exception e) {
////             TODO Auto-generated catch block
//            e.printStackTrace();
//        }

//    }
    public void addDB(){
        ContentValues cv = new ContentValues();
        cv.put(DataDB.NUMBER,strPhoneNumber);
        cv.put(DataDB.LATITUDE,mFinalChoosePosition.latitude);
        cv.put(DataDB.LONGITUDE,mFinalChoosePosition.longitude);
        cv.put(DataDB.ADDRS,strAddrs);
        if(getIntent().getBooleanExtra("friendly",true))
            cv.put(DataDB.Friendly,1);
        else
            cv.put(DataDB.Friendly,0);
        Log.i("insertInfo",cv.toString());
        mDatabase.insert(DataDB.TABLE_NAME,null,cv);
    }
}