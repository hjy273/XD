package com.tyb.xd.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tyb.xd.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TimePicker extends LinearLayout {
    private static final int UPDATE_TITLE_MSG = 0x111;
    private static final int UPDATE_WHEEL = 0x112;
    private static final int UPDATE_UpdateHour_MSG = 0x113;
    private static final int UPDATE_UpdateMinute_MSG = 0x114;
    private WheelView mWheelMinute;
    private WheelView mWheelDay;
    private WheelView mWheelHour;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mCurrentDay;
    private int mHour;
    private int mCurrentHour;
    private int mMinute;
    private int mCurrentMinute;
    private int layout_id;
    private Calendar mCalendar;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_TITLE_MSG: {
                    /**
                     * 执行更新数据
                     */
                }
                break;
                case UPDATE_WHEEL: {
                    updateWheel();
                }
                break;
                case UPDATE_UpdateHour_MSG: {
                    updateHour(mCurrentDay);
                    updateMinute(mCurrentHour);
                }
                break;
                case UPDATE_UpdateMinute_MSG: {
                    updateMinute(mCurrentHour);
                }
                break;
            }
        }
    };
    private WheelView.OnSelectListener mMinuteListener = new WheelView.OnSelectListener() {
        @Override
        public void endSelect(int minute, String text) {
            if (mCurrentDay == mDay && mCurrentHour == mHour) {
                mMinute = mCurrentMinute + minute;
            } else {
                mMinute = minute + 1;
            }
        }

        @Override
        public void selecting(int id, String text) {
        }
    };
    private WheelView.OnSelectListener mDayListener = new WheelView.OnSelectListener() {
        @Override
        public void endSelect(int day, String text) {
            mDay = day + mCurrentDay;
            mHandler.sendEmptyMessage(0x113);
        }

        @Override
        public void selecting(int day, String text) {
        }
    };
    private WheelView.OnSelectListener mHourListener = new WheelView.OnSelectListener() {
        @Override
        public void endSelect(int hour, String text) {
            if (mCurrentDay == mDay) {
                mHour = mCurrentHour + hour;
            } else {
                mHour = hour;
            }
            mHandler.sendEmptyMessage(0x114);
        }

        @Override
        public void selecting(int day, String text) {
        }
    };
    private Context mContext;

    public TimePicker(Context context) {
        this(context, null);
    }

    public TimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TimePicker);
        int lenght = array.getIndexCount();
        for (int i = 0; i < lenght; i++) {
            int attr = array.getIndex(i);
            if (attr == R.styleable.TimePicker_layout_id) {
                layout_id = array.getResourceId(attr, R.layout.time_picker);
            }
        }
        array.recycle();
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContext = getContext();
        LayoutInflater.from(mContext).inflate(layout_id, this);
        mWheelMinute = (WheelView) findViewById(R.id.minute);
        mWheelDay = (WheelView) findViewById(R.id.day);
        mWheelHour = (WheelView) findViewById(R.id.hour);
        mWheelMinute.setOnSelectListener(mMinuteListener);
        mWheelDay.setOnSelectListener(mDayListener);
        mWheelHour.setOnSelectListener(mHourListener);
    }

    private void updateHour(int day) {
        mWheelHour.resetData(getHourData(mCurrentHour));
        mWheelHour.setDefault(0);
        mHour = 0;
    }

    private void updateMinute(int hour) {
        mWheelMinute.resetData(getMinuteData());
        mWheelMinute.setDefault(0);
        mMinute = 0;
    }

    /**
     * set WLQQTimePicker date
     *
     * @param date
     */
    public void setDate(long date) {
        mCalendar = Calendar.getInstance(Locale.CHINA);
        mCalendar.setTimeInMillis(date);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mCurrentDay = mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mCurrentHour = mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mCurrentMinute = mMinute = mCalendar.get(Calendar.MINUTE);
        mWheelMinute.setData(getMinuteData());
        mWheelDay.setData(getDayData(mCurrentDay, mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)));
        mWheelHour.setData(getHourData(mCurrentHour));
        mHandler.sendEmptyMessage(UPDATE_TITLE_MSG);
        mHandler.sendEmptyMessage(UPDATE_WHEEL);
    }

    private void updateWheel() {
        mWheelMinute.setDefault(0);
        mWheelDay.setDefault(0);
        mWheelHour.setDefault(0);
    }


    private ArrayList<String> getDayData(int startDay, int endDay) {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = startDay; i <= endDay; i++) {
            list.add(i + "日");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, mMonth + 1);
        for (int i = 1; i <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            list.add("次月" + i + "日");
        }
        return list;
    }

    private ArrayList<String> getHourData(int hour) {
        ArrayList<String> list = new ArrayList<String>();
        if (mCurrentDay == mDay) {
            for (int i = hour; i < 24; i++) {
                list.add(i + "时");
            }
        } else {
            for (int i = 0; i < 24; i++) {
                list.add(i + "时");
            }
        }
        return list;
    }

    private ArrayList<String> getMinuteData() {
        ArrayList<String> list = new ArrayList<String>();
        if (mCurrentDay == mDay && mCurrentHour == mHour) {
            for (int i = mCurrentMinute; i < 60; i++) {
                list.add(i + "分");
            }
        } else {
            for (int i = 1; i < 60; i++) {
                list.add(i + "分");
            }
        }
        return list;
    }

    public String toString() {
        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        if (mDay > mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            mMonth++;
            int timeDay  = mDay - mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            String time = mYear + "-" + ((mMonth < 10) ? "0" + mMonth : mMonth + "") + "-" +
                    ((timeDay < 10) ? "0" + timeDay : timeDay + "") + " " + ((mHour < 10) ? "0" + mHour : mHour + "") + ":" + ((mMinute < 10) ? "0" + mMinute : mMinute + "");
            return time;
        } else {
            String time = mYear + "-" + ((mMonth < 10) ? "0" + mMonth : mMonth + "") + "-" +
                    ((mDay < 10) ? "0" + mDay : mDay + "") + " " + ((mHour < 10) ? "0" + mHour : mHour + "") + ":" + ((mMinute < 10) ? "0" + mMinute : mMinute + "");
            return time;
        }
    }
}