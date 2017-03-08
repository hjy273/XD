package com.tyb.xd.utils;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.tyb.xd.bean.Citys;
import com.tyb.xd.bean.PlaceBean;
import com.tyb.xd.bean.Schools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by wangpeiyu on 2016/7/29.
 */
public class Placeutil {

    private static PlaceBean mPlaceBean;

    private static Schools mSchool;

    public static void initData(Context context) {
        StringBuilder sBuild = new StringBuilder();
        try {
            InputStream input = context.getResources().getAssets().open("PlaceData.json");
            InputStreamReader inReader = new InputStreamReader(input);
            BufferedReader bufferedReader = new BufferedReader(inReader);
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                sBuild.append(line);
            }
            mPlaceBean = JSON.parseObject(sBuild.toString(),PlaceBean.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取全部学校的全部信息
     *
     * @param context
     * @return
     */
    public static PlaceBean getPlace(Context context) {
        if (mPlaceBean == null) {
            initData(context);
        }
        return mPlaceBean;
    }

    /**
     * 根据学校名获取学校的地点信息
     *
     * @param context
     * @param schoolname
     * @return
     */
    public static Schools getSchool(Context context, String schoolname) {
        if (mSchool == null) {
            if (mPlaceBean == null) {
                initData(context);
            }
            if (mPlaceBean != null) {
                for (Citys city : mPlaceBean.getCitys()) {
                    for (Schools s : city.getSchools()) {
                        if (s.equals(schoolname)) {
                            mSchool = s;
                            return mSchool;
                        }
                    }
                }
            }
            return null;
        } else {
            return mSchool;
        }
    }
}
