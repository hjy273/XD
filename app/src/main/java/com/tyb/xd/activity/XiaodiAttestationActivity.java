package com.tyb.xd.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tyb.xd.R;
import com.tyb.xd.utils.Util;
import com.tyb.xd.view.PlacePicker;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import butterknife.BindView;
import butterknife.OnClick;

@ContentView(R.layout.ac_xiaodi_attestation)
public class XiaodiAttestationActivity extends Activity {

    @ViewInject(R.id.id_top_back_iv_img)
    ImageView mivGoback;
    @ViewInject(R.id.id_top_tv_content)
    TextView mtvTopContent;
    @ViewInject(R.id.id_top_rl)
    RelativeLayout mtlTop;
    @ViewInject(R.id.id_ac_xiaodi_attestation_btn_address)
    Button mbtnXiaodiAttestation;
    @ViewInject(R.id.id_ac_xiaodi_attestation_et_dormitory)
    EditText metDormitory;
    @ViewInject(R.id.id_ac_xiaodi_attestation_btn_apply)
    Button mbtnApply;
    @ViewInject(R.id.id_ac_xiaodi_attestation_pgb_progress)
    ProgressBar mpgbProgress;

    private PopupWindow mPopSelectPlace;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    private String mstrAddress = "";
    private String mstrDormitory = "";


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x110) {
                mbtnXiaodiAttestation.setText(mstrAddress);
            }
        }
    };

    @Event({R.id.id_top_back_iv_img, R.id.id_ac_xiaodi_attestation_btn_address, R.id.id_ac_xiaodi_attestation_btn_apply})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_top_back_iv_img:
                finish();
                break;
            case R.id.id_ac_xiaodi_attestation_btn_address:
                showPopSelectPlace();
                break;
            case R.id.id_ac_xiaodi_attestation_btn_apply:
                mstrDormitory = metDormitory.getText().toString();
                if (TextUtils.isEmpty(mstrAddress)) {
                    Toast.makeText(mContext, getResources().getString(R.string.choose_address), Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mstrDormitory)) {
                    Toast.makeText(mContext, getResources().getString(R.string.write_dormitory), Toast.LENGTH_SHORT).show();
                } else {
                    mpgbProgress.setVisibility(View.VISIBLE);
                    String url = XDApplication.dbUrl+"/fulltimeuser/request";
                    RequestParams requestParams = new RequestParams(url);
                    requestParams.addBodyParameter("username",XDApplication.getmUser().getmUsername());
                    requestParams.addBodyParameter("token",XDApplication.getmUser().getmToken());
                    x.http().post(requestParams, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            mpgbProgress.setVisibility(View.GONE);
                            JSONObject jsonObject = JSON.parseObject(result);
                            if(jsonObject.getString("status").equals("success"))
                            {
                                Toast.makeText(mContext,getResources().getString(R.string.apply_is_send),Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(mContext,getResources().getString(R.string.apply_fail),Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            mpgbProgress.setVisibility(View.GONE);
                            Toast.makeText(mContext,getResources().getString(R.string.apply_fail),Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onCancelled(CancelledException cex) {
                        }
                        @Override
                        public void onFinished() {

                        }
                    });

                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);
        initView();
    }

    private void initView() {
        mContext = XiaodiAttestationActivity.this;
        mLayoutInflater = LayoutInflater.from(mContext);
        mtvTopContent.setText(getResources().getString(R.string.xiaodi_attestation));
        mivGoback.setVisibility(View.VISIBLE);
    }

    private void showPopSelectPlace() {
        if (mPopSelectPlace == null) {
            View view = mLayoutInflater.inflate(R.layout.pop_select_place, null);
            mPopSelectPlace = new PopupWindow(view, Util.dpToPx(getResources(), 320),
                    LinearLayout.LayoutParams.WRAP_CONTENT, true);
            initPopSelectPlace(view);
        }
        mPopSelectPlace.showAtLocation(mbtnXiaodiAttestation, Gravity.CENTER, 0, 0);
    }


    private void initPopSelectPlace(View view) {
        final PlacePicker placePicker = (PlacePicker) view.findViewById(R.id.id_pop_select_place_tp_place);
        Button btnCancel = (Button) view.findViewById(R.id.id_pop_select_place_cancel);
        Button btnConfirm = (Button) view.findViewById(R.id.id_pop_select_place_confirm);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopSelectPlace.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mstrAddress = placePicker.toString();
                mHandler.sendEmptyMessage(0x110);
                mPopSelectPlace.dismiss();
            }
        });
        mPopSelectPlace.setOutsideTouchable(true);
        mPopSelectPlace.setAnimationStyle(android.R.style.Animation_InputMethod);
    }

}
