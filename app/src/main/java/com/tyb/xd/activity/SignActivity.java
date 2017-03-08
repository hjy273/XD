package com.tyb.xd.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tyb.xd.R;
import com.tyb.xd.adapter.CommonAdapter;
import com.tyb.xd.adapter.ViewHolder;
import com.tyb.xd.bean.SignDataBean;
import com.tyb.xd.bean.User;
import com.tyb.xd.utils.Errorutils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


@ContentView(R.layout.ac_sign)
public class SignActivity extends Activity {
    @ViewInject(R.id.id_sign_gv_date)
    private GridView mgvDate;
    @ViewInject(R.id.id_sign_tv_month)
    private TextView mtvMonth;
    @ViewInject(R.id.id_top_back_iv_img)
    private ImageView mivTopback;
    @ViewInject(R.id.id_top_tv_content)
    private TextView mtvTopSign;
    @ViewInject(R.id.id_top_rl)
    private RelativeLayout mrlTop;
    @ViewInject(R.id.id_sign_tv_all_signed_nums)
    private TextView mtvAllSignedNums;
    @ViewInject(R.id.id_sign_earn_xd_grade)
    private TextView mtvSignEarned;

    private CommonAdapter mAdapter;
    private List<SignDataBean> marrDate;
    private User mUser;
    private Context mContext;
    private List<String> mDateSigned;

    @Event(value = {R.id.id_ac_sign_in_btn_confirm, R.id.id_top_back_iv_img})
    private void btnOnClick(View v) {
        switch (v.getId()) {
            case R.id.id_ac_sign_in_btn_confirm:
                sign();
                break;
            case R.id.id_top_back_iv_img:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
        }
    }

    /**
     * 签到方法
     */
    private void sign() {
        final Calendar cal = Calendar.getInstance();
        final int day = cal.get(Calendar.DAY_OF_MONTH);//本日
        if (mDateSigned.contains(day + "")) {
            Toast.makeText(mContext, getResources().getString(R.string.not_sign_repeat), Toast.LENGTH_SHORT).show();
            return;
        }
        String signUrl = XDApplication.dbUrl + "/sign";
        RequestParams params = new RequestParams(signUrl);
        params.addBodyParameter("username", mUser.getmUsername());
        params.addBodyParameter("token", mUser.getmToken());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    String status = json.getString("status");
                    if (status.equals("success")) {
                        Toast.makeText(mContext, getResources().getString(R.string.sign_success), Toast.LENGTH_SHORT).show();
                        cal.set(Calendar.DAY_OF_MONTH, 1);
                        int first_day_of_week = cal.get(Calendar.DAY_OF_WEEK);//一号是一周的第几天
                        int num = day + first_day_of_week + 5;
                        marrDate.get(num).setSigned(true);
                        mDateSigned.add(day + "");
                        setSignData();
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.sign_fail), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(mContext, getResources().getString(R.string.sign_fail), Toast.LENGTH_SHORT).show();
                Errorutils.showXutilError(mContext, ex);
                Errorutils.showError(mContext, ex, "sign", "SignActivity", SignActivity.this);
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        x.view().inject(this);
        init();
    }

    /**
     * 初始化
     */
    public void init() {
        mUser = XDApplication.getmUser();
        mContext = getApplicationContext();
        /**
         * 初始化控件
         */
        mivTopback.setImageResource(R.drawable.go_back_white);
        mtvTopSign.setText(getResources().getString(R.string.sign));
        mrlTop.setBackgroundColor(Color.TRANSPARENT);
        mDateSigned = new ArrayList<>();

        /**
         *初始化签到数据
         */
        setAllDate();

        String signUrl = XDApplication.dbUrl + "/sign";
        RequestParams params = new RequestParams(signUrl);
        params.addBodyParameter("username", mUser.getmUsername());
        params.addBodyParameter("token", mUser.getmToken());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    String status = json.getString("status");
                    if (status.equals("success")) {
                        JSONArray array = json.getJSONArray("record");
                        for (int i = 0; i < array.length(); i++) {
                            mDateSigned.add(array.get(i).toString());
                        }
                        setSignData();
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.data_loading_fail), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Errorutils.showXutilError(mContext, ex);
                Errorutils.showError(mContext, ex, "init", "SignActivity", SignActivity.this);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    /**
     * 设置签到日期及其他数据
     */
    private void setSignData() {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int first_day_of_week = cal.get(Calendar.DAY_OF_WEEK);//一号是一周的第几天

        String num = mDateSigned.size() + "";
        int oneEarn = 1;
        mtvAllSignedNums.setText(num);
        String allEarn = oneEarn * (Integer.valueOf(num)) + "";
        mtvSignEarned.setText(allEarn);
        for (int i = 0; i < mDateSigned.size(); i++) {
            int n = Integer.valueOf(mDateSigned.get(i)) + first_day_of_week + 5;
            marrDate.get(n).setSigned(true);
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 初始所有日期
     */
    private void setAllDate() {

        marrDate = new ArrayList<>();
        /**
         * 初始化星期标题
         */
        String[] week_date = getResources().getStringArray(R.array.week_date);
        for (String date : week_date) {
            SignDataBean dateObj = new SignDataBean(0, date, false);
            marrDate.add(dateObj);
        }
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.SUNDAY);

        int month = cal.get(Calendar.MONTH) + 1;//本月
        int day_nums = cal.getActualMaximum(Calendar.DATE);//本月天数

        String s_month = month + "月";
        mtvMonth.setText(s_month);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        int first_day_of_week = cal.get(Calendar.DAY_OF_WEEK);//一号是一周的第几天

        cal.add(Calendar.DAY_OF_MONTH, -1);
        int last_month_day_nums = cal.get(Calendar.DAY_OF_MONTH);//上个月最大天数
        /**
         * 设置上月日期
         */
        for (int i = 1; i < first_day_of_week; i++) {
            String date = (last_month_day_nums - (first_day_of_week - i - 1)) + "";
            SignDataBean dateObj = new SignDataBean(2, date, false);
            marrDate.add(dateObj);
        }
        /**
         * 设置本月日期
         */
        for (int i = 1; i < day_nums + 1; i++) {
            String date = i + "";
            SignDataBean dateObj = new SignDataBean(1, date, false);
            marrDate.add(dateObj);
        }
        /**
         * 设置下月日期
         */
        for (int i = 1; i < 44 - day_nums - first_day_of_week; i++) {
            String date = i + "";
            SignDataBean dateObj = new SignDataBean(2, date, false);
            marrDate.add(dateObj);
        }
        mgvDate.setEnabled(false);
        mAdapter = new CommonAdapter<SignDataBean>(getApplicationContext(), marrDate, R.layout.items_grid_sign) {

            @Override
            public void convert(ViewHolder helper, SignDataBean item) {
                helper.setText(R.id.id_sign_tv_date, item.getDate());
                if (item.getType() == 0) {
                    helper.setTextSize(R.id.id_sign_tv_date, 12);
                } else if (item.getType() == 2) {
                    helper.setTextColor(R.id.id_sign_tv_date, getResources().getColor(R.color.gray));
                }
                if (item.isSigned()) {
                    helper.setTextBackground(R.id.id_sign_tv_date, ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_stroke_green_50));
                }
            }
        };
        mgvDate.setAdapter(mAdapter);
    }

    /**
     * 重写返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return true;
        }
        return false;
    }
}
