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
import com.tyb.xd.bean.GoOutBean;
import com.tyb.xd.fastbean.GoOutDeliveryDetailWithoutReceiver;
import com.tyb.xd.fastbean.GoOutDetailRoot;
import com.tyb.xd.fragment.FgCommunicate;
import com.tyb.xd.utils.Errorutils;
import com.tyb.xd.utils.TimeUtils;
import com.tyb.xd.view.refreshListView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import butterknife.BindView;
import butterknife.OnClick;

@ContentView(R.layout.ac_go_out_desctiption)
public class GoOutDetail extends Activity {
    @ViewInject(R.id.id_top_back_iv_img)
    ImageView mivTopBack;
    @ViewInject(R.id.id_top_back_tv)
    TextView mtvTopBack;
    @ViewInject(R.id.id_top_tv_content)
    TextView mtvTopContent;
    @ViewInject(R.id.id_top_rl)
    RelativeLayout mrlTop;
    @ViewInject(R.id.id_ac_go_out_des_iv_head_picture)
    ImageView mivUserImg;
    @ViewInject(R.id.id_ac_go_out_des_tv_username)
    TextView mtvUsername;
    @ViewInject(R.id.id_ac_go_out_des_tv_grade)
    TextView mtvGrade;
    @ViewInject(R.id.id_ac_go_out_des_tv_smile_point)
    TextView mtvReward;
    @ViewInject(R.id.id_ac_go_out_des_tv_startplace)
    TextView mtvStartPlace;
    @ViewInject(R.id.id_ac_go_out_des_tv_endplace)
    TextView mtvEndPlace;
    @ViewInject(R.id.id_ac_go_out_des_tv_end_time)
    TextView mtvLimitTime;
    @ViewInject(R.id.id_ac_go_out_des_tv_des)
    TextView mtvDes;
    @ViewInject(R.id.id_ac_go_out_des_btn_contact)
    Button mbtnContact;
    private Context mContext;
    private String mId;
    GoOutDetailRoot mData;
    private static final int COMPLETE = 0x110;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case COMPLETE:
                    if (mData != null) {
                        GoOutDeliveryDetailWithoutReceiver delivery = mData.getDelivery();
                        ImageOptions options = new ImageOptions.Builder()
                                .setFailureDrawableId(R.drawable.good_type_express)
                                .setLoadingDrawableId(R.drawable.good_type_express)
                                .setCircular(true)
                                .build();
                        x.image().bind(mivUserImg, delivery.getPublisher().getHeadimg() , options);
                        mtvUsername.setText(delivery.getPublisher().getNickname());
                        mtvStartPlace.setText(delivery.getSource());
                        mtvEndPlace.setText(delivery.getDestination());
                        mtvLimitTime.setText(delivery.getDeadline());
                        mtvReward.setText(delivery.getReward() + "");
                        mtvGrade.setText(delivery.getPublisher().getCredibility() + "");
                    }
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
        initData();
    }


    private void initData() {
        String url = XDApplication.dbUrl + "/delivery/outing/" + mId;
        RequestParams requestParams = new RequestParams(url);
        requestParams.addParameter("token", XDApplication.getmUser().getmToken());
        requestParams.addParameter("username", XDApplication.getmUser().getmUsername());
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.getString("status").equals("success")) {
                    mData = JSON.parseObject(result, GoOutDetailRoot.class);
                    mHandler.sendEmptyMessage(COMPLETE);
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


    private void initView() {
        mContext = GoOutDetail.this;
        Bundle bundle = getIntent().getExtras();
        GoOutBean goOutBean = (GoOutBean) bundle.getSerializable("goout");
        mivTopBack.setImageResource(R.drawable.go_back_white);
        mivTopBack.setVisibility(View.VISIBLE);
        mtvTopBack.setText(mContext.getResources().getString(R.string.hall));
        mtvTopContent.setText(mContext.getResources().getString(R.string.goout_detail));
        ImageOptions options = new ImageOptions.Builder()
                .setFailureDrawableId(R.drawable.default_headimg)
                .setLoadingDrawableId(R.drawable.default_headimg)
                .setCircular(true)
                .build();
        x.image().bind(mivUserImg, goOutBean.getHeadimgUrl() , options);
        mtvUsername.setText(goOutBean.getUserName());
        mtvStartPlace.setText(goOutBean.getStartPlace());
        mtvEndPlace.setText(goOutBean.getEndPlace());
        mtvLimitTime.setText(goOutBean.getLimitTime());
        mtvTopBack.setTextColor(mContext.getResources().getColor(R.color.text_color_white));
        mtvReward.setText(goOutBean.getReward());
        mId = goOutBean.getId();
    }

    @Event(value = {R.id.id_top_back_iv_img, R.id.id_top_back_tv, R.id.id_ac_go_out_des_btn_contact})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_top_back_iv_img:
            case R.id.id_top_back_tv:
                finish();
                break;
            case R.id.id_ac_go_out_des_btn_contact:
                if (XDApplication.jurisdiction(mContext)) {
                    if (mData != null) {
                        if (mData.getDelivery().getState() < 3) {
                            if (mData.getDelivery().getPublisher().getNickname().equals(XDApplication.getmUser().getmUsername())) {
                                Toast.makeText(mContext,
                                        mContext.getResources().getString(R.string.can_not_contact_yourself),
                                        Toast.LENGTH_SHORT).show();
                            } else {//自己不是发布者，再判断是否是兼职笑递员
                                if (XDApplication.getmUser().getmRole().equals("fulltime")) {
                                    if (!TimeUtils.TimeOverTenMinute(mData.getDelivery().getTime(), TimeUtils.getTime(System.currentTimeMillis()))) {
                                        Toast.makeText(mContext, mContext.getResources().getString(R.string.full_time_xd_need_ten_minutes_can_get), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                                String url = XDApplication.dbUrl + "/delivery/outing/" + mId;
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
                                            Bundle bundle_goout = new Bundle();
                                            bundle_goout.putString("id", FgCommunicate.SHOWGO + mId);
                                            bundle_goout.putString("touser", mData.getDelivery().getPublisher().getNickname());
                                            bundle_goout.putString("username", mData.getDelivery().getPublisher().getUsername());
                                            bundle_goout.putString("from", FgCommunicate.ADDMSG);
                                            intent.putExtras(bundle_goout);
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable ex, boolean isOnCallback) {
                                        Errorutils.showXutilError(mContext, ex);
                                        Errorutils.showError(mContext, ex, null, null, null);
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
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.goout_have_carry), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
    }
}
