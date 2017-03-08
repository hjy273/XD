package com.tyb.xd.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.tyb.xd.R;
import com.tyb.xd.activity.XDApplication;
import com.tyb.xd.bean.Campuss;
import com.tyb.xd.bean.Schools;
import com.tyb.xd.bean.Types;
import com.tyb.xd.utils.Placeutil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PlacePicker extends LinearLayout {
    private static final int UPDATE_WHEEL = 0x112;
    private static final int UPDATE_TYPES_MSG = 0x113;
    private static final int UPDATE_PLACE_MSG = 0x114;
    private WheelView mCampus;
    private WheelView mTypes;
    private WheelView mPlace;
    private int miCampus = 0;
    private int miTypes = 0;
    private int miPlace = 0;
    private List<ArrayList<ArrayList<String>>> mPlaceData = new ArrayList<ArrayList<ArrayList<String>>>();
    private List<ArrayList<String>> mTypesData = new ArrayList<ArrayList<String>>();
    private ArrayList<String> mCampusData = new ArrayList<String>();
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
                case UPDATE_TYPES_MSG: {
                    updateTypes();
                    updatePlace();
                }
                break;
                case UPDATE_PLACE_MSG:
                    updatePlace();
                    break;
            }
        }
    };
    private WheelView.OnSelectListener mCampusListener = new WheelView.OnSelectListener() {
        @Override
        public void endSelect(int campus, String text) {
            miCampus = campus;
            mHandler.sendEmptyMessage(0x113);
        }

        @Override
        public void selecting(int id, String text) {
        }
    };
    private WheelView.OnSelectListener mTypesListener = new WheelView.OnSelectListener() {
        @Override
        public void endSelect(int types, String text) {
            miTypes = types;
            mHandler.sendEmptyMessage(0x114);
        }

        @Override
        public void selecting(int day, String text) {
        }
    };
    private WheelView.OnSelectListener mPlaceListener = new WheelView.OnSelectListener() {
        @Override
        public void endSelect(int place, String text) {
            miPlace = place;
        }

        @Override
        public void selecting(int day, String text) {
        }
    };
    private Activity mContext;

    public PlacePicker(Context context) {
        this(context, null);
    }

    public PlacePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = (Activity) getContext();
        initData();
    }

    private void initData() {
        Schools schools = Placeutil.getSchool(mContext, TextUtils.isEmpty(XDApplication.getmUser().getmSchool()) ? "重庆大学" : XDApplication.getmUser().getmSchool());
        for (Campuss campuss : schools.getCampuss()) {
            mCampusData.add(campuss.getCampus());
            ArrayList<String> typestemp = new ArrayList<String>();
            ArrayList<ArrayList<String>> placetemp = new ArrayList<ArrayList<String>>();
            for (Types types : campuss.getTypes()) {
                typestemp.add(types.getType());
                ArrayList<String> temp = new ArrayList<String>();
                for (String place : types.getSpecific_place()) {
                    temp.add(place);
                }
                placetemp.add(temp);
            }
            mTypesData.add(typestemp);
            mPlaceData.add(placetemp);
        }
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContext = (Activity) getContext();
        LayoutInflater.from(mContext).inflate(R.layout.place_picker, this);
        mCampus = (WheelView) findViewById(R.id.id_campus);
        mTypes = (WheelView) findViewById(R.id.id_type);
        mPlace = (WheelView) findViewById(R.id.id_specific_place);
        mCampus.setOnSelectListener(mCampusListener);
        mTypes.setOnSelectListener(mTypesListener);
        mPlace.setOnSelectListener(mPlaceListener);
        mCampus.setData(mCampusData);
        mTypes.setData(mTypesData.get(0));
        mPlace.setData(mPlaceData.get(0).get(0));
        mHandler.sendEmptyMessage(0x112);
    }

    private void updateTypes() {
        mTypes.resetData(mTypesData.get(miCampus));
        mTypes.setDefault(0);
        miTypes = 0;
    }

    private void updatePlace() {
        mPlace.resetData(mPlaceData.get(miCampus).get(miTypes));
        mPlace.setDefault(0);
        miPlace = 0;
    }

    private void updateWheel() {
        mCampus.setDefault(0);
        mTypes.setDefault(0);
        mPlace.setDefault(0);
    }

    public String toEntireString() {
        return XDApplication.getmUser().getmSchool() + mCampusData.get(miCampus) + mPlaceData.get(miCampus).get(miTypes).get(miPlace);
    }

    @Override
    public String toString() {
        return mCampusData.get(miCampus) + mPlaceData.get(miCampus).get(miTypes).get(miPlace);
    }
}