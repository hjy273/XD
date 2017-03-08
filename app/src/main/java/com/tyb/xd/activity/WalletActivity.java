package com.tyb.xd.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionProvider;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tyb.xd.R;
import com.tyb.xd.adapter.CommonAdapter;
import com.tyb.xd.adapter.ViewHolder;
import com.tyb.xd.bean.User;
import com.tyb.xd.bean.WeekCostBean;
import com.tyb.xd.bean.WeekCostItemBean;
import com.tyb.xd.utils.Errorutils;
import com.tyb.xd.utils.SharePreferenceUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

@ContentView(R.layout.ac_wallet)
public class WalletActivity extends Activity {

    @ViewInject(R.id.id_top_tv_content)
    private TextView mtvTopContent;
    @ViewInject(R.id.id_ac_wallet_tv_total_money)
    private TextView mtvTotalMoney;
    @ViewInject(R.id.id_ac_wallet_tv_gold_money)
    private TextView mtvGoldMoney;
    @ViewInject(R.id.id_ac_wallet_tv_silver_money)
    private TextView mtvSilverMoney;
    @ViewInject(R.id.id_top_back_iv_img)
    private ImageView mivTopBack;
    @ViewInject(R.id.id_ac_wallet_lv_one_week_cost_recard)
    private ListView mlvCostRecord;
    @ViewInject(R.id.id_ac_wallet_iv_retract)
    private ImageButton mivRetract;
    private Context mContext;
    private User mUser;
    private CommonAdapter<WeekCostItemBean> mAdapter;
    private WeekCostBean mCostBean;
    private String code;

    @Event(value = {R.id.id_ac_wallet_ll_record, R.id.id_ac_wallet_iv_retract, R.id.id_top_back_iv_img, R.id.id_ac_wallet_ll_recharge, R.id.id_ac_wallet_ll_withdraw, R.id.id_ac_wallet_ll_invite})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_top_back_iv_img:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case R.id.id_ac_wallet_ll_recharge:
                toNextActivity(WalletActivity.this, RechargeActivity.class);
                break;
            case R.id.id_ac_wallet_ll_withdraw:
                toNextActivity(WalletActivity.this, WithdrawActivity.class);
                break;
            case R.id.id_ac_wallet_ll_invite:
                invite();
                break;
            case R.id.id_ac_wallet_ll_record:
            case R.id.id_ac_wallet_iv_retract:
                if (mlvCostRecord.getVisibility() == View.INVISIBLE) {
                    mlvCostRecord.setVisibility(View.VISIBLE);
                    mivRetract.setBackground(getResources().getDrawable(R.drawable.go_to_up_gray));
                } else {
                    mlvCostRecord.setVisibility(View.INVISIBLE);
                    mivRetract.setBackground(getResources().getDrawable(R.drawable.go_to_down_gray));
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        x.view().inject(this);
        initView();
    }

    private void initView() {
        mContext = WalletActivity.this;
        mUser = XDApplication.getmUser();
        //初始化标题栏
        mtvTopContent.setText(getResources().getString(R.string.wallet));
        mivTopBack.setImageResource(R.drawable.go_back_white);
        //初始化控件数据
        WelcomeActivity.complete_Info(mContext);
        float goldPoint = mUser.getmGoldMoney();
        float silverPoint = mUser.getmSilverMoney();
        float totalPoint = goldPoint + silverPoint;
        String sgoldPoint = goldPoint + "";
        String ssilverPoint = silverPoint + "";
        String stotalPoint = totalPoint + "";
        mtvGoldMoney.setText(sgoldPoint);
        mtvSilverMoney.setText(ssilverPoint);
        mtvTotalMoney.setText(stotalPoint);
        //设置adapter
        initAdapter();
        getCode();
    }

    /**
     * 获取邀请码
     */
    private void getCode() {
        if (SharePreferenceUtils.getLoginStatus(mContext)) {
            String url = XDApplication.dbUrl + "/user/self";
            RequestParams params = new RequestParams(url);
            params.addBodyParameter("username", mUser.getmUsername());
            params.addBodyParameter("token", mUser.getmToken());
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    JSONObject json = JSON.parseObject(result);
                    String status = json.getString("status");
                    if (status.equals("success")) {
                        JSONObject jsonUser = json.getJSONObject("user");
                        code = jsonUser.getString("code");
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
        } else {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.no_login), Toast.LENGTH_SHORT).show();
        }

    }

    private void initAdapter() {
        String url = XDApplication.dbUrl + "/user/self/pointrecords";
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("username", mUser.getmUsername());
        params.addBodyParameter("token", mUser.getmToken());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                mCostBean = JSON.parseObject(result, WeekCostBean.class);
                List<WeekCostItemBean> records = mCostBean.getRecords();
                mAdapter = new CommonAdapter<WeekCostItemBean>(mContext, records, R.layout.wallet_item) {
                    @Override
                    public void convert(ViewHolder helper, WeekCostItemBean item) {
                        helper.setText(R.id.id_ac_wallet_tv_cost_date, item.getTime());
                        helper.setText(R.id.id_ac_wallet_tv_balance, item.getBalance());
                        helper.setText(R.id.id_ac_wallet_tv_money_change, item.getNumber());
                        helper.setText(R.id.id_ac_wallet_tv_cost_type, item.getType());
                    }
                };
                mlvCostRecord.setAdapter(mAdapter);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
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

    private void toNextActivity(Activity a, Class b) {
        if (SharePreferenceUtils.getLoginStatus(mContext)) {
            Intent intent = new Intent(a, b);
            startActivity(intent);
        } else {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.no_login), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(a, LoginActivity.class);
            startActivity(intent);
        }

    }

    /**
     * 邀请好友
     */
    private void invite() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        String text = "我的邀请码是" + code + ",注册可以获赠笑点哦！";
        oks.setText(text);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        // oks.setImagePath("/sdcard/test.jpg");
        // 确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");
        // 启动分享GUI
        oks.show(this);
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
