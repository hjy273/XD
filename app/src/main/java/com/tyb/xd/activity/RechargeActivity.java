package com.tyb.xd.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tyb.xd.R;
import com.tyb.xd.bean.User;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import c.b.BP;
import c.b.PListener;
import c.b.QListener;

/**
 *
 */
@ContentView(R.layout.ac_recharge)
public class RechargeActivity extends Activity {
    @ViewInject(R.id.id_top_tv_content)
    private TextView mtvTopContent;
    @ViewInject(R.id.id_top_back_iv_img)
    private ImageView mivTopBack;
    @ViewInject(R.id.id_top_rl)
    private RelativeLayout mRlTop;
    @ViewInject(R.id.id_ac_recharge_tv_username)
    private TextView mtvUsername;
    @ViewInject(R.id.id_ac_recharge_tv_phone)
    private EditText metPhone;
    @ViewInject(R.id.id_ac_recharge_ll_five)
    private LinearLayout mllFive;
    @ViewInject(R.id.id_ac_recharge_ll_eight)
    private LinearLayout mllEight;
    @ViewInject(R.id.id_ac_recharge_ll_fifteen)
    private LinearLayout mllFifteen;
    @ViewInject(R.id.id_ac_recharge_ll_twenty_five)
    private LinearLayout mllTwentyFive;
    @ViewInject(R.id.id_ac_recharge_ll_fifty)
    private LinearLayout mllFifty;
    @ViewInject(R.id.id_ac_recharge_ll_one_hundred)
    private LinearLayout mllOneHundred;
    @ViewInject(R.id.id_ac_recharge_btn_recharge)
    private Button mbtnPay;

    private ProgressDialog mpdTip;

    private HashMap<Integer, String> mError = new HashMap<Integer, String>();

    /**
     * 支付相关
     */
    private final static int FIVE = 5;
    private final static int EIGHT = 8;
    private final static int FIFTEEN = 15;
    private final static int TWENTY_FIVE = 25;
    private final static int FIFTY = 50;
    private final static int ONE_HUNDRED = 100;
    private int mRmbLevel = FIVE;
    // 此为支付插件的官方最新版本号,请在更新时留意更新说明
    int PLUGINVERSION = 7;
    private String mOrderId = "";


    private int mColor = Color.parseColor("#00dec9");
    private Context mContext;
    private User mUser;

    @Event(value = {R.id.id_top_back_iv_img, R.id.id_ac_recharge_ll_eight, R.id.id_ac_recharge_ll_five,
            R.id.id_ac_recharge_ll_fifteen, R.id.id_ac_recharge_ll_fifty, R.id.id_ac_recharge_ll_twenty_five,
            R.id.id_ac_recharge_ll_one_hundred, R.id.id_ac_recharge_btn_recharge})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_top_back_iv_img:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case R.id.id_ac_recharge_ll_five:
                mRmbLevel = FIVE;
                resetImg();
                mllFive.setBackgroundColor(mColor);
                break;
            case R.id.id_ac_recharge_ll_eight:
                mRmbLevel = EIGHT;
                resetImg();
                mllEight.setBackgroundColor(mColor);
                break;
            case R.id.id_ac_recharge_ll_fifteen:
                mRmbLevel = FIFTEEN;
                resetImg();
                mllFifteen.setBackgroundColor(mColor);
                break;
            case R.id.id_ac_recharge_ll_twenty_five:
                mRmbLevel = TWENTY_FIVE;
                resetImg();
                mllTwentyFive.setBackgroundColor(mColor);
                break;
            case R.id.id_ac_recharge_ll_fifty:
                mRmbLevel = FIFTY;
                resetImg();
                mllFifty.setBackgroundColor(mColor);
                break;
            case R.id.id_ac_recharge_ll_one_hundred:
                mRmbLevel = ONE_HUNDRED;
                resetImg();
                mllOneHundred.setBackgroundColor(mColor);
                break;
            case R.id.id_ac_recharge_btn_recharge:
                Toast.makeText(mContext,"客户端不支持充值",Toast.LENGTH_SHORT).show();
                /*
                if (XDApplication.jurisdiction(this)) {
                    if (TextUtils.isEmpty(metPhone.getText().toString())) {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.pay_count_is_not_null), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (metPhone.getText().toString().startsWith("1") && metPhone.getText().toString().length() == 11) {
                        pay(mRmbLevel);
                    } else {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.pay_count_is_not_legal), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }*/
                break;
        }
    }

    /**
     * 调用支付
     * 支付类型，true为支付宝支付,false为微信支付
     */
    private void pay(int mRmbLevel) {
        showDialog("正在获取订单...");
        final String descrip = getPayDescrip(mRmbLevel);
        String mstrName = "";
        switch (mRmbLevel) {
            case FIVE:
                mstrName = "500笑点";
                break;
            case EIGHT:
                mstrName = "820笑点";
                break;
            case FIFTEEN:
                mstrName = "1550笑点";
                break;
            case TWENTY_FIVE:
                mstrName = "2575笑点";
                break;
            case FIFTY:
                mstrName = "5100笑点";
                break;
            case ONE_HUNDRED:
                mstrName = "10200笑点";
                break;
        }
        BP.pay(mstrName, descrip, mRmbLevel, false, new PListener() {

            // 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
            @Override
            public void unknow() {
/*                showTip(mContext.getResources().getString(R.string.pay_result_unknow));
                hideDialog();*/
            }

            // 支付成功,如果金额较大请手动查询确认
            @Override
            public void succeed() {
/*                Toast.makeText(mContext, mContext.getString(R.string.pay_success), Toast.LENGTH_SHORT).show();
                showTip(mContext.getResources().getString(R.string.pay_success));
                hideDialog();*/
            }

            // 无论成功与否,返回订单号
            @Override
            public void orderId(String orderId) {
                // 此处应该保存订单号,比如保存进数据库等,以便以后查询
                mOrderId = orderId;
                query();
            }

            // 支付失败,原因可能是用户中断支付操作,也可能是网络原因
            @Override
            public void fail(int code, String reason) {
                // 当code为-2,意味着用户中断了操作
                // code为-3意味着没有安装BmobPlugin插件
                if (code == -3) {
                    Toast.makeText(
                            mContext,
                            mContext.getString(R.string.pay_fail_need_plugin),
                            Toast.LENGTH_SHORT).show();
                    installBmobPayPlugin("bp.db");
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.pay_interrupt), Toast.LENGTH_SHORT)
                            .show();
                }
                showTip(getReason(code));
            }
        });
    }

    private String getReason(int code) {
        return mError.get(code);
    }

    private void resetImg() {
        mllFive.setBackgroundResource(R.drawable.rectangle_light_blue);
        mllEight.setBackgroundResource(R.drawable.rectangle_light_blue);
        mllFifteen.setBackgroundResource(R.drawable.rectangle_light_blue);
        mllTwentyFive.setBackgroundResource(R.drawable.rectangle_light_blue);
        mllFifty.setBackgroundResource(R.drawable.rectangle_light_blue);
        mllOneHundred.setBackgroundResource(R.drawable.rectangle_light_blue);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        x.view().inject(this);
        mContext = RechargeActivity.this;
        BP.init(this, "253950cf6fe3d5d8682b0f4e0251ef63");
        loadError();
        vertifyPluginVersion();
        initView();
    }

    private void loadError() {
        mError.put(-1, "未安装微信");
        mError.put(-2, "微信支付用户中断操作");
        mError.put(-3, "未安装支付插件");
        mError.put(1111, "网络错误");
        mError.put(2222, "网络错误");
        mError.put(3333, "网络错误");
        mError.put(5277, "订单不存在");
        mError.put(7777, "微信客户端未安装");
        mError.put(8888, "微信客户端版本不支持微信支付");
        mError.put(9010, "网络异常");
        mError.put(10777, "请稍后...");
        mError.put(150, "订单号是空的");
        mError.put(10002, "订单不存在");
        mError.put(10003, "余额不足");
        mError.put(10004, "余额不足");
    }

    private void vertifyPluginVersion() {
        int pluginVersion = BP.getPluginVersion();
        if (pluginVersion < PLUGINVERSION) {// 为0说明未安装支付插件, 否则就是支付插件的版本低于官方最新版
            Toast.makeText(
                    mContext,
                    pluginVersion == 0 ? mContext.getResources().getString(R.string.pay_no_plugin)
                            : mContext.getResources().getString(R.string.pay_plugin_is_not_lastest), Toast.LENGTH_SHORT).show();
            installBmobPayPlugin("bp.db");
        }
    }

    private void initView() {
        mContext = RechargeActivity.this;
        mUser = XDApplication.getmUser();
        //初始化标题栏
        mtvTopContent.setText(getResources().getString(R.string.recharge));
        mivTopBack.setImageResource(R.drawable.go_back_white);
        mtvUsername.setText(mUser.getmUsername());
        metPhone.setText(mUser.getmUserPhone());
        mllFive.setBackgroundColor(mColor);
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

    private String getPayDescrip(int type) {
        String strName = "";
        strName = "xd_" + XDApplication.getmUser().getmUsername() + "_pay" + System.currentTimeMillis() + "_" + type;
        return strName;
    }

    void installBmobPayPlugin(String fileName) {
        try {
            InputStream is = getAssets().open(fileName);
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + fileName + ".apk");
            if (file.exists())
                file.delete();
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + file),
                    "application/vnd.android.package-archive");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void hideDialog() {
        if (mpdTip != null && mpdTip.isShowing())
            try {
                mpdTip.dismiss();
            } catch (Exception e) {
            }
    }

    void showDialog(String message) {
        try {
            if (mpdTip == null) {
                mpdTip = new ProgressDialog(this);
                mpdTip.setCancelable(true);
            }
            mpdTip.setMessage(message);
            mpdTip.show();
        } catch (Exception e) {
            // 在其他线程调用dialog会报错
        }
    }

    private void showTip(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
    }

    // 执行订单查询
    private void query() {
        mpdTip.dismiss();
        final String orderId = mOrderId;
        Boolean isSuccess = false;
        BP.query(orderId, new QListener() {
            @Override
            public void succeed(String status) {
                if (status.equals("SUCCESS")) {
                    showTip(mContext.getResources().getString(R.string.pay_success));
                    finish();
                } else {
                    showTip(mContext.getResources().getString(R.string.pay_fail));
                }
            }

            @Override
            public void fail(int code, String reason) {
                showTip(getReason(code));
            }
        });
    }
}
