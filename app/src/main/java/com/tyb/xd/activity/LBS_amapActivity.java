package com.tyb.xd.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.tyb.xd.R;
import com.tyb.xd.fragment.FgHall;
import com.tyb.xd.handler.getOutingsRoundAsyn;
import com.tyb.xd.handler.getTasksRoundAsyn;
import com.tyb.xd.interfacelistener.getTasksRoundListener;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;


@ContentView(R.layout.ac_amap)
public class LBS_amapActivity extends CheckPermissionsActivity implements LocationSource, AMapLocationListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, getTasksRoundListener {
    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    @ViewInject(R.id.id_ac_amap_map)
    private MapView mMapView;

    private AMap mMap;

    private boolean mIsFirst = true;

    /**
     * 定位相关
     *
     * @param savedInstanceState
     */
    private AMapLocationClient mLocationClient;

    private AMapLocationClientOption mLocationOption;

    private OnLocationChangedListener mOnLocationChangedListener;
    /**
     * 获取附近任务的异步线程
     */
    private getTasksRoundAsyn mGetTasksRoundAsyn;

    private Context mContext;

    private int miContentType;
    private getOutingsRoundAsyn mGetOutingsRoundAsyn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        bundle.putStringArray("permission", needPermissions);
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);
        Bundle extras = getIntent().getExtras();
        miContentType = extras.getInt("type");
        mContext = LBS_amapActivity.this;
        /**
         * 定位按钮
         */
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，
        // 实现地图生命周期管理
        mMapView.onCreate(savedInstanceState);
        init();
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (mMap == null) {
            mMap = mMapView.getMap();
            setUpMap();
            startGetTasksAsyn();
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 定义定位图标
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.myposition));// 设置我的位置的图标
        myLocationStyle.strokeColor(Color.TRANSPARENT);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);// 设置圆形的填充颜色
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        mMap.setMyLocationStyle(myLocationStyle);
        mMap.setLocationSource(this);// 设置定位监听
        mMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        mMap.setMyLocationEnabled(true);
        // 显示比例尺
        mMap.getUiSettings().setScaleControlsEnabled(true);
        //设置缩放比例
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16f));
    }

    private void startGetTasksAsyn() {
        switch (miContentType) {
            case FgHall.CONTENT_REWARD:
                mGetTasksRoundAsyn = new getTasksRoundAsyn(this);
                mGetTasksRoundAsyn.execute();
                break;
            case FgHall.CONTENT_GOOUT:
                mGetOutingsRoundAsyn = new getOutingsRoundAsyn(this);
                mGetOutingsRoundAsyn.execute();
                break;
        }

        /**m
         * 设置mark点击事件
         */
        //    mMap.setInfoWindowAdapter(this);
        mMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
    }


    @Override
    protected void onDestroy() {
        deactivate();
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mIsFirst = true;
        mMapView.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，
        // 实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }


    /**
     * 定位成功后回调函数
     *
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mOnLocationChangedListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {
                // 显示定位图标
                mOnLocationChangedListener.onLocationChanged(aMapLocation);
                /**
                 * 手动定位并显示图标
                 */
                //定位成功回调信息，设置相关消息
                aMapLocation.getLatitude();//获取纬度
                aMapLocation.getLongitude();//获取经度
                aMapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (mIsFirst) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
                    mMap.moveCamera(cameraUpdate);
                    mIsFirst = false;
                } else {

                }
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.location_fail), Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * LocationSource方法
     * 用于在按下地图上的“我的位置”按钮时进行回调
     *
     * @param onLocationChangedListener
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mOnLocationChangedListener = onLocationChangedListener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();
        }
    }

    /**
     * LocationSource方法
     */
    @Override
    public void deactivate() {
        mOnLocationChangedListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }


    @Override
    public void addMarks(HashMap<String, Integer> list, HashMap<String, HashMap<String, Float>> positon) {
        /**
         * 根据返回的悬赏信息，在地图上标记出来
         */
        // 动画效果
        Set<String> strings = list.keySet();
        int i = 2;
        for (String s : strings) {
            ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
            giflist.add(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_RED));
            giflist.add(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            giflist.add(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            MarkerOptions options = new MarkerOptions();
            options.anchor(0.5f, 0.5f)
                    .position(new LatLng(positon.get(s).get("lat"), positon.get(s).get("lng")))
                    .icons(giflist)
                    .title(s)
                    .snippet("悬赏有 " + list.get(s))
                    .visible(true)
                    .period(1);
            Marker marker = mMap.addMarker(options);
            marker.showInfoWindow();
            i++;
        }
    }


    /**
     * 自定义的Infowindow
     *
     * @param marker
     * @return
     */
    @Override
    public View getInfoWindow(Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.amapinfowindow, null);
        TextView mtvNumber = (TextView) view.findViewById(R.id.id_amap_info_number);
        mtvNumber.append(marker.getTitle() + " " + marker.getSnippet());
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        FgHall.msKeyWord = marker.getTitle().substring(marker.getTitle().indexOf("校区")+1);
        FgHall.isFromAmap = true;
        finish();
    }
}
