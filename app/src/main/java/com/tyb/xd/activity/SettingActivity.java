package com.tyb.xd.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.tyb.xd.R;
import com.tyb.xd.utils.SharePreferenceUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.OnClick;

@ContentView(R.layout.ac_setting)
public class SettingActivity extends Activity {
    @ViewInject(R.id.id_top_back_iv_img)
    ImageView mivTopBack;
    @ViewInject(R.id.id_top_tv_content)
    TextView mtvTopContent;
    @ViewInject(R.id.id_top_rl)
    RelativeLayout mrlTop;
    @ViewInject(R.id.id_setting_rl_real_name)
    RelativeLayout mrlRealName;
    @ViewInject(R.id.id_setting_rl_msg_setting)
    RelativeLayout mrlMsgSetting;
    @ViewInject(R.id.id_setting_rl_aboutxd)
    RelativeLayout mrlAboutXd;
    @ViewInject(R.id.id_setting_rl_exit_account)
    RelativeLayout mrlExit;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        x.view().inject(this);
        initView();
    }

    private void initView() {
        mContext = SettingActivity.this;
        mivTopBack.setImageResource(R.drawable.go_back_white);
        mtvTopContent.setText(mContext.getResources().getString(R.string.setting));
    }

    @Event(value = {R.id.id_top_back_iv_img, R.id.id_setting_rl_password_reset,
            R.id.id_setting_rl_msg_setting, R.id.id_setting_rl_aboutxd,
            R.id.id_setting_rl_exit_account, R.id.id_setting_rl_real_name})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_top_back_iv_img:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case R.id.id_setting_rl_password_reset:
                toNext(PassResetActivity.class);
                break;
            case R.id.id_setting_rl_msg_setting:
                toNext(MsgSettingActivity.class);
                break;
            case R.id.id_setting_rl_aboutxd:
                toNext(AboutXdActivity.class);
                break;
            case R.id.id_setting_rl_real_name:
                toNext(RealNameVertifyActivity.class);
                break;
            case R.id.id_setting_rl_exit_account:
                if(SharePreferenceUtils.getLoginStatus(mContext))
                {
                    SharePreferenceUtils.setLoginStatu(mContext, false);
                    XDApplication.getmUser().resetInfo();
                    if (XDApplication.avimClient != null) {
                        XDApplication.avimClient.close(new AVIMClientCallback() {
                            @Override
                            public void done(AVIMClient avimClient, AVIMException e) {
                                XDApplication.avimClient = null;
                            }
                        });
                    }
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(mContext,mContext.getResources().getString(R.string.no_login),Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void toNext(Class<?> next) {
        Intent intent = new Intent(mContext, next);
        startActivity(intent);
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
