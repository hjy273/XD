package com.tyb.xd.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.tyb.xd.R;
import com.tyb.xd.activity.RechargeActivity;
import com.tyb.xd.activity.XDApplication;
import com.tyb.xd.fragment.FgHall;
import com.tyb.xd.utils.Errorutils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 后台进行不断的定位
 */
public class LocalLocationService extends CheckPermissionService {

    private MyLocationThread mMyLocationThread;
    Context mContext = this;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (mMyLocationThread != null) {
            if (!mMyLocationThread.isAlive()) {
                mMyLocationThread.start();
            }
        } else {
            mMyLocationThread = new MyLocationThread();
        }
        return new LocalLocationBinderProxy(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mMyLocationThread != null) {
            if (!mMyLocationThread.isAlive())
                mMyLocationThread.start();
        } else {
            mMyLocationThread = new MyLocationThread();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        mMyLocationThread = new MyLocationThread();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMyLocationThread.setRun(false);
        mMyLocationThread.setStopLocation();
        mMyLocationThread = null;
    }

    @Override
    public void onRebind(Intent intent) {
        if (!mMyLocationThread.isAlive()) {
            mMyLocationThread.start();
        } else {
            mMyLocationThread = new MyLocationThread();
        }
        super.onRebind(intent);
    }

    /**
     * 后台执行的线程
     * 实时上传自己的经纬度
     * 并获取我的周边的悬赏
     * 将信息传递出去并发生通知
     */
    public class MyLocationThread extends Thread implements AMapLocationListener {

        public boolean isRun = true;

        private AMapLocationClient mMapClient;

        private AMapLocationClientOption mMapOption;

        private Double mLat = 0d;

        private Double mLng = 0d;

        public void setRun(boolean run) {
            isRun = run;
        }

        public void setStopLocation() {
            mMapClient.stopLocation();
            mMapClient = null;
        }

        @Override
        public void run() {
            initMap();
            /**
             * 执行我的位置经纬度信息上传
             * 获取到悬赏信息
             * 筛选悬赏信息（即判断悬赏是否在我的周围）
             * 利用结果返回
             * 并提醒
             * 用于悬赏列表
             */
            String url_task = XDApplication.dbUrl + "/amap/delivery/task";
            RequestParams params = new RequestParams(url_task);
            params.addBodyParameter("school", "重庆大学");
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    try {
                        int num = 0;
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("deliveries");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject deliver = jsonArray.getJSONObject(i);
                                String lng = deliver.getString("lng");
                                String lat = deliver.getString("lat");
                                float[] results = new float[1];
                                Location.distanceBetween(Double.parseDouble(lat), Double.parseDouble(lng), mLat, mLng, results);
                                if (results[0] * 1609.344 <= 100) {
                                    num++;
                                }
                            }
                            if (num > 0) {
                                String text = "您的附近有" + String.valueOf(num) + "条代送悬赏哦~";
                                String title = "代送悬赏提醒";
                                showNotify(title, text);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Errorutils.showError(mContext, ex, "initView", "LocalLocationService", this);
                    Errorutils.showXutilError(mContext, ex);

                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });

        }

        private void showNotify(String title, String text) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(mContext);
            Intent notificationIntent = new Intent(mContext, FgHall.class);
            PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
            builder.setContentIntent(contentIntent);
            builder.setContentTitle(title);
            builder.setContentText(text);
            builder.setSmallIcon(R.drawable.login_logo);
            mNotificationManager.notify(1, builder.build());
        }

        private void initMap() {
            mMapClient = new AMapLocationClient(getApplicationContext());
            mMapOption = new AMapLocationClientOption();
            mMapOption.setNeedAddress(true);
            //每个五分钟进行定位一次
            mMapOption.setInterval(300000);
            mMapOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mMapClient.setLocationOption(mMapOption);
            mMapClient.setLocationListener(this);
            mMapClient.startLocation();//开启定位
        }

        /**
         * 定位后进行回调
         *
         * @param aMapLocation
         */
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                XDApplication.mLat = mLat = aMapLocation.getLatitude();//获取纬度
                XDApplication.mLng = mLng = aMapLocation.getLongitude();//获取经度
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
            }
        }
    }
}


