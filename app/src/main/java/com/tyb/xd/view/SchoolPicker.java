package com.tyb.xd.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.tyb.xd.R;
import com.tyb.xd.activity.XDApplication;
import com.tyb.xd.bean.Campuss;
import com.tyb.xd.bean.Citys;
import com.tyb.xd.bean.Schools;
import com.tyb.xd.bean.Types;
import com.tyb.xd.utils.Placeutil;

import java.util.ArrayList;
import java.util.List;

public class SchoolPicker extends LinearLayout {
    private static final int UPDATE_WHEEL = 0x112;
    private static final int UPDATE_SCHOOL_MSG = 0x113;
    private static final int UPDATE_CAMPUS_MSG = 0x114;
    private WheelView mCity;
    private WheelView mSchool;
    private WheelView mCampus;
    private int miCity = 0;
    private int miSchool = 0;
    private int miCampus = 0;
    private List<ArrayList<ArrayList<String>>> mCampusData = new ArrayList<ArrayList<ArrayList<String>>>();
    private List<ArrayList<String>> mSchoolData = new ArrayList<ArrayList<String>>();
    private ArrayList<String> mCityData = new ArrayList<String>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_WHEEL: {
                    /**
                     * 执行更新数据
                     */
                    updateWheel();
                }
                break;
                case UPDATE_SCHOOL_MSG: {
                    updateSchool();
                    updateCampus();
                }
                break;
                case UPDATE_CAMPUS_MSG:
                    updateCampus();
                    break;
            }
        }
    };
    private WheelView.OnSelectListener mCityListener = new WheelView.OnSelectListener() {
        @Override
        public void endSelect(int city, String text) {
            miCity = city;
            mHandler.sendEmptyMessage(0x113);
        }

        @Override
        public void selecting(int id, String text) {
        }
    };
    private WheelView.OnSelectListener mSchoolListener = new WheelView.OnSelectListener() {
        @Override
        public void endSelect(int school, String text) {
            miSchool = school;
            mHandler.sendEmptyMessage(0x114);
        }

        @Override
        public void selecting(int day, String text) {
        }
    };
    private WheelView.OnSelectListener mCampusListener = new WheelView.OnSelectListener() {
        @Override
        public void endSelect(int campus, String text) {
            miCampus = campus;
        }

        @Override
        public void selecting(int day, String text) {
        }
    };
    private Activity mContext;

    public SchoolPicker(Context context) {
        this(context, null);
    }

    public SchoolPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = (Activity) getContext();
        initData();
    }

    private void initData() {
        for (Citys  citys : Placeutil.getPlace(mContext).getCitys()) {
            mCityData.add(citys.getCity());
            ArrayList<String> typestemp = new ArrayList<String>();
            ArrayList<ArrayList<String>> placetemp = new ArrayList<ArrayList<String>>();
            for (Schools schools : citys.getSchools()) {
                typestemp.add(schools.getSchool());
                ArrayList<String> temp = new ArrayList<String>();
                for (Campuss campuss :schools.getCampuss()) {
                    temp.add(campuss.getCampus());
                }
                placetemp.add(temp);
            }
            mSchoolData.add(typestemp);
            mCampusData.add(placetemp);
        }
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContext = (Activity) getContext();
        LayoutInflater.from(mContext).inflate(R.layout.place_picker, this);
        mCity = (WheelView) findViewById(R.id.id_campus);
        mSchool = (WheelView) findViewById(R.id.id_type);
        mCampus = (WheelView) findViewById(R.id.id_specific_place);
        mCity.setOnSelectListener(mCityListener);
        mSchool.setOnSelectListener(mSchoolListener);
        mCampus.setOnSelectListener(mCampusListener);


        mCity.setData(mCityData);
        mSchool.setData(mSchoolData.get(0));
        mCampus.setData(mCampusData.get(0).get(0));
        mHandler.sendEmptyMessage(0x112);
    }

    private void updateSchool() {
        mSchool.resetData(mSchoolData.get(miCity));
        mSchool.setDefault(0);
        miSchool = 0;
    }

    private void updateCampus() {
        mCampus.resetData(mCampusData.get(miCity).get(miSchool));
        mCampus.setDefault(0);
        miCampus = 0;
    }

    private void updateWheel() {
        mCity.setDefault(0);
        mSchool.setDefault(0);
        mCampus.setDefault(0);
    }

    @Override
    public String toString() {
        return mSchoolData.get(miCity).get(miSchool) +"-"+mCampusData.get(miCity).get(miSchool).get(miCampus);
    }
}