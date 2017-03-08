package com.tyb.xd.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tyb.xd.R;
import com.tyb.xd.utils.SharePreferenceUtils;
import com.tyb.xd.view.SwitchView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import butterknife.BindView;
import butterknife.OnClick;

@ContentView(R.layout.ac_msg_setting)
public class MsgSettingActivity extends Activity {

    @ViewInject(R.id.id_top_back_iv_img)
    ImageView mivTopBack;
    @ViewInject(R.id.id_top_tv_content)
    TextView mtvTopContent;
    @ViewInject(R.id.id_top_rl)
    RelativeLayout mrlTop;
    @ViewInject(R.id.id_ac_msg_setting_sv_sound)
    SwitchView msvSound;
    @ViewInject(R.id.id_ac_msg_setting_sv_shake)
    SwitchView msvShake;
    private Context mContext;

    @Event(value = {R.id.id_top_back_iv_img})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_top_back_iv_img:
                finish();
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
        mContext = MsgSettingActivity.this;
        mivTopBack.setImageResource(R.drawable.go_back_white);
        mtvTopContent.setText(mContext.getResources().getString(R.string.msg_setting));
        boolean soundSetting = SharePreferenceUtils.getUserSetting(mContext, XDApplication.getmUser().getmUserPhone(), SharePreferenceUtils.SOUND);
        boolean shakeSetting = SharePreferenceUtils.getUserSetting(mContext, XDApplication.getmUser().getmUserPhone(), SharePreferenceUtils.SHAKE);
        msvShake.setState(shakeSetting);
        msvSound.setState(soundSetting);
        msvShake.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                msvShake.toggleSwitch(true);
                SharePreferenceUtils.setUserSetting(mContext,
                        XDApplication.getmUser().getmUserPhone(), true, SharePreferenceUtils.SHAKE);
            }

            @Override
            public void toggleToOff() {
                msvShake.toggleSwitch(false);
                SharePreferenceUtils.setUserSetting(mContext,
                        XDApplication.getmUser().getmUserPhone(), false, SharePreferenceUtils.SHAKE);
            }
        });
        msvSound.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                msvSound.toggleSwitch(true);
                SharePreferenceUtils.setUserSetting(mContext,
                        XDApplication.getmUser().getmUserPhone(), true, SharePreferenceUtils.SOUND);
            }
            @Override
            public void toggleToOff() {
                msvSound.toggleSwitch(false);
                SharePreferenceUtils.setUserSetting(mContext,
                        XDApplication.getmUser().getmUserPhone(), false, SharePreferenceUtils.SOUND);
            }
        });
    }
}
