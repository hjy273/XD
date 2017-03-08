package com.tyb.xd.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tyb.xd.R;
import com.tyb.xd.bean.User;
import com.tyb.xd.utils.Errorutils;
import com.tyb.xd.utils.SharePreferenceUtils;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.reflect.Method;

/**
 */
@ContentView(R.layout.ac_withdraw)
public class WithdrawActivity extends Activity {
    @ViewInject(R.id.id_ac_withdraw_tv_bind)
    private TextView mtvBind;
    @ViewInject(R.id.id_ac_withdraw_ll_750)
    private LinearLayout mll750;
    @ViewInject(R.id.id_ac_withdraw_ll_1000)
    private LinearLayout mll1000;
    @ViewInject(R.id.id_ac_withdraw_ll_1500)
    private LinearLayout mll1500;
    @ViewInject(R.id.id_ac_withdraw_ll_2500)
    private LinearLayout mll2500;
    @ViewInject(R.id.id_ac_withdraw_ll_4000)
    private LinearLayout mll4000;
    @ViewInject(R.id.id_ac_withdraw_ll_6000)
    private LinearLayout mll6000;
    @ViewInject(R.id.id_ac_recharge_btn_withdraw)
    private Button mbtnWithdraw;
    @ViewInject(R.id.id_top_tv_content)
    private TextView mtvTopContent;
    @ViewInject(R.id.id_top_back_iv_img)
    private ImageView mivTopBack;
    @ViewInject(R.id.id_top_rl)
    private RelativeLayout mRlTop;
    @ViewInject(R.id.id_ac_withdraw_tv_username)
    private TextView mtvAccount;
    @ViewInject(R.id.id_ac_withdraw_et_airpay_count)
    private EditText metAliPayAcount;
    @ViewInject(R.id.id_ac_withdraw_tv_balance)
    private TextView mtvBalance;
    private Context mContext;
    private User mUser;

    private String mstrBindAlipayAcount = "";

    private int mColor = Color.parseColor("#00dec9");
    int miSmilePointAcount = SMILEPOINT_WITHDRAW_LEVEL1;
    /**
     * 笑点提现等级
     */
    private static final int SMILEPOINT_WITHDRAW_LEVEL1 = 750;
    private static final int SMILEPOINT_WITHDRAW_LEVEL2 = 1000;
    private static final int SMILEPOINT_WITHDRAW_LEVEL3 = 1500;
    private static final int SMILEPOINT_WITHDRAW_LEVEL4 = 2500;
    private static final int SMILEPOINT_WITHDRAW_LEVEL5 = 4000;
    private static final int SMILEPOINT_WITHDRAW_LEVEL6 = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        x.view().inject(this);
        initView();
    }

    private void initView() {
        mContext = WithdrawActivity.this;
        mUser = XDApplication.getmUser();
        //初始化标题栏
        mtvTopContent.setText(getResources().getString(R.string.withdraw));
        mivTopBack.setImageResource(R.drawable.go_back_white);
        mtvAccount.setText(mUser.getmNickname());
        getAlipayAcount();
        /**
         *初始等级
         */
        mll750.setBackgroundColor(mColor);
        //可以转换的最高笑点
        mtvBalance.setText(XDApplication.getmUser().getmGoldMoney() + "");
        metAliPayAcount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 13) {
                    metAliPayAcount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                } else {
                    metAliPayAcount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                }
                if (!s.toString().equals(mstrBindAlipayAcount)) {
                    mtvBind.setText(mContext.getResources().getString(R.string.no_bind));
                } else {
                    mtvBind.setText(mContext.getResources().getString(R.string.bind));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Event({R.id.id_ac_withdraw_tv_bind, R.id.id_ac_withdraw_ll_750,
            R.id.id_ac_withdraw_ll_1000, R.id.id_ac_withdraw_ll_1500,
            R.id.id_ac_withdraw_ll_2500, R.id.id_ac_withdraw_ll_4000, R.id.id_ac_withdraw_ll_6000,
            R.id.id_ac_recharge_btn_withdraw, R.id.id_top_back_iv_img})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_top_back_iv_img:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case R.id.id_ac_withdraw_tv_bind:
                if (mtvBind.getText().toString().equals(mContext.getResources().getString(R.string.no_bind))) {
                    bindAirpayAccount();
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.bind), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.id_ac_withdraw_ll_750:
                resetImg();
                miSmilePointAcount = SMILEPOINT_WITHDRAW_LEVEL1;
                mll750.setBackgroundColor(mColor);
                break;
            case R.id.id_ac_withdraw_ll_1000:
                miSmilePointAcount = SMILEPOINT_WITHDRAW_LEVEL2;
                resetImg();
                mll1000.setBackgroundColor(mColor);
                break;
            case R.id.id_ac_withdraw_ll_1500:
                miSmilePointAcount = SMILEPOINT_WITHDRAW_LEVEL3;
                resetImg();
                mll1500.setBackgroundColor(mColor);
                break;
            case R.id.id_ac_withdraw_ll_2500:
                miSmilePointAcount = SMILEPOINT_WITHDRAW_LEVEL4;
                resetImg();
                mll2500.setBackgroundColor(mColor);
                break;
            case R.id.id_ac_withdraw_ll_4000:
                miSmilePointAcount = SMILEPOINT_WITHDRAW_LEVEL5;
                resetImg();
                mll4000.setBackgroundColor(mColor);
                break;
            case R.id.id_ac_withdraw_ll_6000:
                miSmilePointAcount = SMILEPOINT_WITHDRAW_LEVEL5;
                resetImg();
                mll6000.setBackgroundColor(mColor);
                break;
            case R.id.id_ac_recharge_btn_withdraw:
                if (SharePreferenceUtils.getLoginStatus(mContext)) {//用户已经登录
                    if (XDApplication.getmUser().ismIsIdentificate()) {//用户已经实名
                        WithDraw();
                    } else {//用户为实名
                        Intent intent = new Intent(mContext, RealNameVertifyActivity.class);
                        startActivity(intent);
                    }
                } else {//用户未登录
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.no_login), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }

                break;
        }
    }


    private void getAlipayAcount() {
        if (XDApplication.jurisdiction(mContext)) {
            String url = XDApplication.dbUrl + "/pay/alipayaccount";
            RequestParams requestParams = new RequestParams(url);
            requestParams.addParameter("username", XDApplication.getmUser().getmUsername());
            requestParams.addParameter("token", XDApplication.getmUser().getmToken());
            x.http().get(requestParams, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    JSONObject jsonObject = JSON.parseObject(result);
                    if (jsonObject.getString("status").equals("success")) {
                        mstrBindAlipayAcount = jsonObject.getString("alipay_account");
                        metAliPayAcount.setText(mstrBindAlipayAcount);
                        mtvBind.setText(mContext.getResources().getString(R.string.bind));
                    } else {
                        metAliPayAcount.setText(XDApplication.getmUser().getmUserPhone());
                        mtvBind.setText(mContext.getResources().getString(R.string.no_bind));
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
        }
    }

    private void bindAirpayAccount() {
        if (!TextUtils.isEmpty(metAliPayAcount.getText().toString())) {//支付宝账号不为空
            String url = XDApplication.dbUrl + "/pay/alipayaccount";
            RequestParams requestParams = new RequestParams(url);
            requestParams.addParameter("username", XDApplication.getmUser().getmUsername());
            requestParams.addParameter("token", XDApplication.getmUser().getmToken());
            requestParams.addParameter("alipay_account", metAliPayAcount.getText().toString());
            x.http().request(HttpMethod.PUT, requestParams, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    JSONObject jsonObject = JSON.parseObject(result);
                    if (jsonObject.getString("status").equals("success")) {
                        mstrBindAlipayAcount = metAliPayAcount.getText().toString();
                        mtvBind.setText(mContext.getResources().getString(R.string.bind));
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.bind_success), Toast.LENGTH_SHORT).show();
                    } else {
                        mtvBind.setText(mContext.getResources().getString(R.string.no_bind));
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.bind_fail), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Errorutils.showXutilError(mContext, ex);
                    Errorutils.showError(mContext, ex, "bindAirpayAccount", "WithdrawActivity", this);
                }

                @Override
                public void onCancelled(CancelledException cex) {
                }

                @Override
                public void onFinished() {

                }
            });
        } else {//支付宝账号为空
            Toast.makeText(mContext, mContext.getResources().getString(R.string.pay_count_is_not_null), Toast.LENGTH_SHORT).show();
        }
    }

    private void resetImg() {
        mll750.setBackgroundResource(R.drawable.rectangle_light_blue);
        mll1000.setBackgroundResource(R.drawable.rectangle_light_blue);
        mll1500.setBackgroundResource(R.drawable.rectangle_light_blue);
        mll2500.setBackgroundResource(R.drawable.rectangle_light_blue);
        mll4000.setBackgroundResource(R.drawable.rectangle_light_blue);
        mll6000.setBackgroundResource(R.drawable.rectangle_light_blue);
    }

    /*****************************************************************************************************/
    private void WithDraw() {
        if (XDApplication.getmUser().getmGoldMoney() >= miSmilePointAcount) {//笑点充足时可以提现
            String url = XDApplication.dbUrl + "/pay/out";
            RequestParams requestParams = new RequestParams(url);
            requestParams.addBodyParameter("username", XDApplication.getmUser().getmUsername());
            requestParams.addBodyParameter("token", XDApplication.getmUser().getmToken());
            requestParams.addBodyParameter("point", miSmilePointAcount + "");
            x.http().post(requestParams, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    JSONObject parse = JSON.parseObject(result);
                    if (parse.getString("status").equals("success")) {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.withdraw_success), Toast.LENGTH_SHORT).show();
                    }
                    XDApplication.getmUser().setmGoldMoney(XDApplication.getmUser().getmGoldMoney() - miSmilePointAcount);
                    mtvBalance.setText(XDApplication.getmUser().getmGoldMoney() + "");
                }
                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.withdraw_fail), Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onCancelled(CancelledException cex) {
                }
                @Override
                public void onFinished() {
                }
            });
        } else {//笑点不足时，进行提示
            Toast.makeText(mContext, mContext.getResources().getString(R.string.smilepoint_lack), Toast.LENGTH_SHORT).show();
        }
    }
}
