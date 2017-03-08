package com.tyb.xd.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tyb.xd.R;
import com.tyb.xd.utils.Errorutils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.OnClick;

@ContentView(R.layout.ac_evaluate)
public class EvaluateActivity extends Activity {

    @ViewInject(R.id.id_top_tv_content)
    private TextView mtvTopContent;
    @ViewInject(R.id.id_top_rl)
    private RelativeLayout mrlTop;
    @ViewInject(R.id.id_ac_evaluate_tv_smilepoint)
    private TextView mtvSmilepoint;
    @ViewInject(R.id.id_ac_evaluate_ratingbar)
    private RatingBar mratingbarEvaluate;
    @ViewInject(R.id.id_ac_evaluate_et_evaluate)
    private EditText metEvaluate;
    @ViewInject(R.id.id_ac_evaluate_btn_confirm)
    private Button mbtnConfirm;

    private Context mContext;
    private Bundle mBundle;

    private String msEvaluate = "";
    private float mfEvaluate = 0f;
    private String msToUser = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);
        initView();
    }

    private void initView() {
        mContext = EvaluateActivity.this;
        mBundle = getIntent().getExtras();
        mrlTop.setBackgroundColor(Color.parseColor("#9dc7f7"));
        mtvTopContent.setText(mContext.getResources().getString(R.string.evaluate));
        mtvSmilepoint.setText(mBundle.getString("reward"));
        msToUser = mBundle.getString("toUser");
        mratingbarEvaluate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mfEvaluate = rating;
            }
        });
    }

    @Event(value = R.id.id_ac_evaluate_btn_confirm)
    private void onClick(View view) {
        msEvaluate = metEvaluate.getText().toString() + "";
        if (mfEvaluate != 0f) {
            try {
                String url = XDApplication.dbUrl + "/assessment/user/" + URLEncoder.encode(msToUser, "utf-8");
                RequestParams requestParams = new RequestParams(url);
                requestParams.addBodyParameter("star", mfEvaluate + "");
                if (!TextUtils.isEmpty(msEvaluate)) {
                    requestParams.addBodyParameter("content", msEvaluate);
                }
                requestParams.addBodyParameter("token", XDApplication.getmUser().getmToken());
                requestParams.addBodyParameter("username", XDApplication.getmUser().getmUsername());
                x.http().post(requestParams, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject jsonObject = JSON.parseObject(result);
                        if (jsonObject.getString("status").equals("success")) {
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.evaluate_success), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.evaluate_fail), Toast.LENGTH_SHORT).show();
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
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.please_give_evaluate), Toast.LENGTH_SHORT).show();
        }
    }
}
