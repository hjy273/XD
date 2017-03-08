package com.tyb.xd.handler;

/**
 * Created by wangpeiyu on 2016/7/12.
 */

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tyb.xd.activity.XDApplication;
import com.tyb.xd.fastbean.AmapDeliveries;
import com.tyb.xd.fastbean.AmapRoot;
import com.tyb.xd.interfacelistener.getTasksRoundListener;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * 异步获取附近任务
 */
public class getTasksRoundAsyn extends AsyncTask<String, String, HashMap<String, Integer>> {
    getTasksRoundListener mgetTaskRoundListener;
    HashMap<String, Integer> list = new HashMap<String, Integer>();
    HashMap<String, HashMap<String, Float>> mPosition = new HashMap<String, HashMap<String, Float>>();

    public getTasksRoundAsyn(getTasksRoundListener mgetTaskRoundListener) {
        this.mgetTaskRoundListener = mgetTaskRoundListener;
    }

    public void setMgetTaskRoundListener(getTasksRoundListener mgetTaskRoundListener) {
        this.mgetTaskRoundListener = mgetTaskRoundListener;
    }

    public getTasksRoundListener getMgetTaskRoundListener() {
        return mgetTaskRoundListener;
    }

    @Override
    protected HashMap<String, Integer> doInBackground(final String... params) {
        /**
         * 执行获取信息操作
         */

        final Semaphore semaphore = new Semaphore(0);
        String url = XDApplication.dbUrl + "/amap/delivery/task";
        final RequestParams requestParams = new RequestParams(url);
        requestParams.addParameter("school", TextUtils.isEmpty(XDApplication.getmUser().getmSchool()) ?
                "重庆大学" : XDApplication.getmUser().getmSchool());
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.getString("status").equals("success")) {
                    AmapRoot amapRoot = JSON.parseObject(result, AmapRoot.class);
                    List<AmapDeliveries> deliveries = amapRoot.getDeliveries();
                    for (AmapDeliveries item : deliveries) {
                        if (list.containsKey(item.getSource())) {
                            Integer integer = list.get(item.getSource());
                            integer++;
                            list.put(item.getSource(), integer);
                        } else {
                            list.put(item.getSource(), 1);
                            HashMap<String, Float> positon = new HashMap<String, Float>();
                            positon.put("lat", Float.parseFloat(item.getLat()));
                            positon.put("lng", Float.parseFloat(item.getLng()));
                            mPosition.put(item.getSource(), positon);
                        }
                    }
                    semaphore.release();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void onPostExecute(HashMap<String, Integer> list) {
        super.onPostExecute(list);
        mgetTaskRoundListener.addMarks(list,mPosition);
    }
}