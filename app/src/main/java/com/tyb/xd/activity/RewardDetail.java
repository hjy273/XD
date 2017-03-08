package com.tyb.xd.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tyb.xd.R;
import com.tyb.xd.bean.RewardBean;
import com.tyb.xd.fastbean.RewardDetailRoot;
import com.tyb.xd.fastbean.RewardRoot;
import com.tyb.xd.fragment.FgCommunicate;
import com.tyb.xd.fragment.FgHall;
import com.tyb.xd.service.BgServicePool;
import com.tyb.xd.utils.Errorutils;
import com.tyb.xd.utils.TimeUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.sql.Time;

import butterknife.BindView;
import butterknife.OnClick;

@ContentView(R.layout.ac_details)
public class RewardDetail extends Activity {
    @ViewInject(R.id.id_top_back_iv_img)
    ImageView mivTopBack;
    @ViewInject(R.id.id_top_back_tv)
    TextView mtvTopBack;
    @ViewInject(R.id.id_top_tv_content)
    TextView mtvTopContent;
    @ViewInject(R.id.id_top_rl)
    RelativeLayout mrlTop;
    @ViewInject(R.id.id_ac_details_iv_good_picture)
    ImageView mivGoodImg;
    @ViewInject(R.id.id_ac_details_tv_grade_num)
    TextView mtvSmilePoint;
    @ViewInject(R.id.id_ac_details_tv_type)
    TextView mtvWeight;
    @ViewInject(R.id.id_ac_details_tv_start_place)
    TextView mtvStartPlace;
    @ViewInject(R.id.id_ac_details_tv_arrive_place)
    TextView mtvEndPlace;
    @ViewInject(R.id.id_ac_details_tv_start_stop_time)
    TextView mtvLimitTime;
    @ViewInject(R.id.id_ac_details_tv_text_des)
    TextView mtvDes;
    @ViewInject(R.id.id_ac_details_iv_head_picture)
    ImageView mivUserImg;
    @ViewInject(R.id.id_ac_details_tv_owner_name)
    TextView mtvUserName;
    @ViewInject(R.id.id_xd_des_tv_real_name)
    TextView mtvHasIdentificate;
    @ViewInject(R.id.id_xd_des_tv_credit)
    TextView mtvCredit;
    @ViewInject(R.id.id_xd_des_btn_receive)
    Button mbtnPublic;
    Context mContext;
    private static final int LOADCOMPLETE = 0x110;
    private RewardDetailRoot mData;
    private String mId;
    private int mContentType;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADCOMPLETE:
                    /**
                     * 数据加载完成执行处理
                     */
                    ImageOptions options = new ImageOptions
                            .Builder()
                            .setLoadingDrawableId(R.drawable.default_headimg)
                            .setFailureDrawableId(R.drawable.default_headimg)
                            .setCircular(true)
                            .build();
                    x.image().bind(mivUserImg, mData.getDelivery().getPublisher().getHeadimg() , options);
                    mtvDes.setText(mData.getDelivery().getDescribe());
                    mtvCredit.setText(mData.getDelivery().getPublisher().getCredibility() + "");
                    mtvUserName.setText(mData.getDelivery().getPublisher().getNickname());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);
        initView();
    }

    private void initView() {
        mContext = RewardDetail.this;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mId = bundle.getString("id");
        mContentType = bundle.getInt("contenttype");
        mivTopBack.setImageResource(R.drawable.go_back_white);
        mtvTopBack.setText(mContext.getResources().getString(R.string.hall));
        mtvTopBack.setTextColor(mContext.getResources().getColor(R.color.text_color_white));
        mtvTopContent.setText(mContext.getResources().getString(R.string.detail));
        RewardBean.setImg(bundle.getString("img"), mivGoodImg, bundle.getString("type"));
        mtvCredit.setText(bundle.getString("reward"));
        mtvWeight.setText(bundle.getString("weight"));
        mtvStartPlace.setText(bundle.getString("startplace"));
        mtvEndPlace.setText(bundle.getString("endplace"));
        mtvLimitTime.setText(bundle.getString("limit_time"));
        loadUserInfo();
    }

    private void loadUserInfo() {
        BgServicePool.getInstance().addRunnable(new Runnable() {
            @Override
            public void run() {
                String url = XDApplication.dbUrl + "/delivery/task/" + mId;
                RequestParams requestParams = new RequestParams(url);
                requestParams.addParameter("username", XDApplication.getmUser().getmUsername());
                requestParams.addParameter("token", XDApplication.getmUser().getmToken());
                x.http().get(requestParams, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject jsonObject = JSON.parseObject(result);
                        if (jsonObject.getString("status").equals("success")) {
                            RewardDetailRoot root = JSON.parseObject(result, RewardDetailRoot.class);
                            mData = root;
                            mHandler.sendEmptyMessage(LOADCOMPLETE);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Errorutils.showXutilError(mContext, ex);
                        Errorutils.showError(mContext, ex, "loadUserInfo", "RewardDetail", RewardDetail.this);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                    }

                    @Override
                    public void onFinished() {
                    }
                });
            }
        });
    }


    @Event(value = {R.id.id_top_back_iv_img, R.id.id_top_back_tv, R.id.id_xd_des_btn_receive})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_top_back_iv_img:
                finish();
            case R.id.id_top_back_tv:
                finish();
                break;
            case R.id.id_xd_des_btn_receive:
                if (XDApplication.jurisdiction(mContext)) {
                    if (mData != null) {
                        if (mData.getDelivery().getState() < 3) {
                            if (mData.getDelivery().getPublisher().getNickname().equals(XDApplication.getmUser().getmUsername())) {
                                Toast.makeText(mContext,
                                        mContext.getResources().getString(R.string.can_not_contact_yourself),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                if (XDApplication.getmUser().getmRole().equals("fulltime")) {
                                    if (!TimeUtils.TimeOverTenMinute(mData.getDelivery().getTime(), TimeUtils.getTime(System.currentTimeMillis()))) {
                                        Toast.makeText(mContext, mContext.getResources().getString(R.string.full_time_xd_need_ten_minutes_can_get), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                                String url = XDApplication.dbUrl + "/delivery/task/" + mId;
                                RequestParams requestParams = new RequestParams(url);
                                requestParams.addBodyParameter("token", XDApplication.getmUser().getmToken());
                                requestParams.addBodyParameter("username", XDApplication.getmUser().getmUsername());
                                x.http().post(requestParams, new Callback.CommonCallback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        JSONObject jsonObject = JSON.parseObject(result);
                                        if (jsonObject.getString("status").equals("success")) {
                                            Toast.makeText(mContext, mContext.getString(R.string.get_success), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(mContext, ChatActivity.class);
                                            Bundle bundle_reward = new Bundle();
                                            bundle_reward.putString("id", FgCommunicate.REWARD + mId);
                                            bundle_reward.putString("touser", mData.getDelivery().getPublisher().getNickname());
                                            bundle_reward.putString("username", mData.getDelivery().getPublisher().getUsername());
                                            bundle_reward.putString("from", FgCommunicate.ADDMSG);
                                            intent.putExtras(bundle_reward);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable ex, boolean isOnCallback) {
                                        Errorutils.showError(mContext, ex, "loadUserInfo", "RewardDetail", RewardDetail.this);
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
                        } else {
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.reward_have_carry), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
    }
}
