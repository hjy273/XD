package com.tyb.xd.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.throrinstudio.android.common.libs.validator.Form;
import com.throrinstudio.android.common.libs.validator.Validate;
import com.throrinstudio.android.common.libs.validator.validator.NotEmptyValidator;
import com.tyb.xd.R;
import com.tyb.xd.bean.User;
import com.tyb.xd.utils.Errorutils;
import com.tyb.xd.utils.SharePreferenceUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import butterknife.BindView;

@ContentView(R.layout.ac_edit_password)
public class PassResetActivity extends Activity {

    @ViewInject(R.id.id_top_back_iv_img)
    ImageView mivTopBack;
    @ViewInject(R.id.id_top_tv_content)
    TextView mtvTopContent;
    @ViewInject(R.id.id_top_rl)
    RelativeLayout mrlTop;
    @ViewInject(R.id.id_ac_editpass_et_oldpass)
    EditText metOldPass;
    @ViewInject(R.id.id_ac_editpass_tv_showerror)
    TextView mtvTip;
    @ViewInject(R.id.id_ac_editpass_et_newpass)
    EditText metNewPass;
    @ViewInject(R.id.id_ac_editpass_et_newpassconfirm)
    EditText metNewPassConfirm;

    private Context mContext;
    private Form mForm;
    private ProgressDialog progressDialog;

    @Event(value = {R.id.id_top_back_iv_img, R.id.id_ac_btn_reset_pass})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_top_back_iv_img:
                finish();
                break;
            case R.id.id_ac_btn_reset_pass:
                resetPass();
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
        mContext = PassResetActivity.this;
        /**
         * 对话框初始化
         */
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle(getResources().getString(R.string.is_reset_pass));

        mivTopBack.setImageResource(R.drawable.go_back_white);
        mtvTopContent.setText(mContext.getResources().getString(R.string.password_reset));
        mForm = new Form();

        /**
         * 表单验证
         */
        Validate validate_oldPass = new Validate(metOldPass);
        validate_oldPass.addValidator(new NotEmptyValidator(mContext));
        Validate validate_newPass = new Validate(metNewPass);
        validate_newPass.addValidator(new NotEmptyValidator(mContext));
        Validate validate_confirmPass = new Validate(metNewPassConfirm);
        validate_confirmPass.addValidator(new NotEmptyValidator(mContext));
        mForm.addValidates(validate_confirmPass);
        mForm.addValidates(validate_newPass);
        mForm.addValidates(validate_oldPass);

    }

    private void resetPass() {
        progressDialog.show();
        if (mForm.validate()) {
            User user = XDApplication.getmUser();
            String password = user.getmUserPass();
            String oldPass = metOldPass.getText().toString();
            final String newpass = metNewPass.getText().toString();
            String confirmPass = metNewPassConfirm.getText().toString();
            if (!password.equals(oldPass)) {
                mtvTip.setVisibility(View.VISIBLE);
            } else if (!newpass.equals(confirmPass)) {
                Toast.makeText(mContext,getResources().getString(R.string.two_pass_different),Toast.LENGTH_SHORT).show();
            } else {
                String token = user.getmToken();
                String username = user.getmUsername();
                String url = XDApplication.dbUrl + "/user/password";
                RequestParams reset = new RequestParams(url);
                reset.addBodyParameter("username", username);
                reset.addBodyParameter("password", newpass);
                reset.addBodyParameter("token",token);
                x.http().post(reset, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject json = JSON.parseObject(result);
                        String status = json.getString("status");
                        if (status.equals("success")) {
                            XDApplication.getmUser().setmUserPass(newpass);
                            SharePreferenceUtils.setCurrLoginUser(mContext,XDApplication.getmUser());
                            SharePreferenceUtils.setCurrUser(mContext,XDApplication.getmUser());
                            Toast.makeText(mContext, getResources().getString(R.string.reset_pass_success), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, getResources().getString(R.string.reset_pass_fail), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Errorutils.showXutilError(mContext, ex);
                        Errorutils.showError(mContext,ex,"resetPass","PassResetActivity",PassResetActivity.this);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }
        }
        progressDialog.dismiss();
    }
}
