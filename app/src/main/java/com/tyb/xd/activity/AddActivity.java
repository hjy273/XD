package com.tyb.xd.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tyb.xd.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import butterknife.BindView;
import butterknife.OnClick;


@ContentView(R.layout.ac_add)
public class AddActivity extends Activity {

    @ViewInject(R.id.id_ac_add_iv_close)
    ImageView mivClose;
    @ViewInject(R.id.id_ac_add_ll_send_reward)
    LinearLayout mllSendReward;
    @ViewInject(R.id.id_ac_add_ll_send_travel)
    LinearLayout mllSendGoOut;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);
        initView();
    }

    private void initView() {
        mContext = AddActivity.this;
    }

    @Event(value = {R.id.id_ac_add_iv_close, R.id.id_ac_add_ll_send_reward, R.id.id_ac_add_ll_send_travel})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_ac_add_iv_close:
                finish();
                break;
            case R.id.id_ac_add_ll_send_reward:
                toNext(SendRewardActivity.class);
                break;
            case R.id.id_ac_add_ll_send_travel:
                toNext(ShowGoOutActivity.class);
                break;
        }
    }

    private void toNext(Class<?> next)
    {
        Intent intent = new Intent(mContext,next);
        startActivity(intent);
        finish();
    }
}
