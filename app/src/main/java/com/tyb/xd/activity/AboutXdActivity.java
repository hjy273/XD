package com.tyb.xd.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tyb.xd.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import butterknife.BindView;

@ContentView(R.layout.ac_about_xd)
public class AboutXdActivity extends Activity {

    @ViewInject(R.id.id_top_back_iv_img)
    ImageView mivTopBack;
    @ViewInject(R.id.id_top_tv_content)
    TextView mtvTopContent;
    @ViewInject(R.id.id_top_rl)
    RelativeLayout mrlTop;
    @ViewInject(R.id.id_ac_about_xd_rl_service)
    RelativeLayout mrlXdService;

    private Context mContext;

    @Event(value = {R.id.id_top_back_iv_img, R.id.id_ac_about_xd_rl_service})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_top_back_iv_img:
                finish();
                break;
            case R.id.id_ac_about_xd_rl_service:
                toNext(XdServiceActivity.class);
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
        mContext = AboutXdActivity.this;
        mtvTopContent.setText(mContext.getResources().getString(R.string.aboutxd));
        mivTopBack.setImageResource(R.drawable.go_back_white);
    }

    private void toNext(Class<?> next)
    {
        Intent intent =  new Intent(mContext,next);
        startActivity(intent);
    }
}
