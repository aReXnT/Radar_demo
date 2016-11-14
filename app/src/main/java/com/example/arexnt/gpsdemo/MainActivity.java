package com.example.arexnt.gpsdemo;

import android.os.Bundle;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
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
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    boolean isFirstLoc = true;// 是否首次定位
    private Button btnLocate;
    private Button btnRefesh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);


//        开启定位图层
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        this.initLocation();
        mLocationClient.start();

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_refresh:
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
                final Animation locateAnim = new AlphaAnimation(.8f,.2f);
                locateAnim.setDuration(2000);
                locateAnim.setRepeatCount(1);
                btnLocate.setAnimation(locateAnim);

                locateAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                        locateCover.setVisibility(View.VISIBLE);
//                        locateCover.setAnimation(locateAnim);
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

        }

    }


    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
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
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);   //设置地图中心点以及缩放级别
                mBaiduMap.animateMapStatus(u);
            }
        }
    };
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
    private void initView(){
        btnLocate.findViewById(R.id.btn_locate);
        btnLocate.setOnClickListener(MainActivity.this);
        btnRefesh.findViewById(R.id.btn_refresh);
        btnRefesh.setOnClickListener(MainActivity.this);
    }
}
