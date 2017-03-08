package com.tyb.xd.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.throrinstudio.android.common.libs.validator.Form;
import com.throrinstudio.android.common.libs.validator.Validate;
import com.throrinstudio.android.common.libs.validator.validator.NotEmptyValidator;
import com.throrinstudio.android.common.libs.validator.validator.PhoneValidator;
import com.tyb.xd.R;
import com.tyb.xd.bean.User;
import com.tyb.xd.utils.Errorutils;

import org.json.JSONException;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;
/**
 * 忘记密码
 */
@ContentView(R.layout.ac_forget_pass)
public class ForgetPasswordActivity extends CheckPermissionsActivity {
    @ViewInject(R.id.id_top_tv_content)
    private TextView mtvTopContent;
    @ViewInject(R.id.id_ac_forget_pass_et_phone)
    private EditText metPhone;
    @ViewInject(R.id.id_ac_forget_pass_et_vertify)
    private EditText metVerify;
    @ViewInject(R.id.id_ac_forget_pass_et_pass)
    private EditText metPass;
    @ViewInject(R.id.id_ac_forget_pass_et_confirmpass)
    private EditText metConfirmPass;
    @ViewInject(R.id.id_top_back_tv)
    private TextView mtvTopBack;
    @ViewInject(R.id.id_top_back_iv_img)
    private ImageView mivTopBack;


    private Form mFormAll, mFormPhone;
    private ProgressDialog mProgressDialog;
    private Context mContext;
    private String msPhone, msVerify, msPass, msConfirmPass;
    private User mUser;

    @Event(value = {R.id.id_ac_forget_pass_btn_get_vertify,R.id.id_top_back_tv, R.id.id_ac_forget_pass_btn_confirm, R.id.id_top_back_iv_img})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_ac_forget_pass_btn_get_vertify:
                mProgressDialog.setTitle(mContext.getResources().getString(R.string.is_getting_vertify));
                if (mFormPhone.validate()) {
                    if (!mProgressDialog.isShowing()) {
                        mProgressDialog.show();
                        msPhone = metPhone.getText().toString();
                        isRegister(msPhone);
                    }
                }
                break;
            case R.id.id_ac_forget_pass_btn_confirm:
                mProgressDialog.setTitle(mContext.getResources().getString(R.string.is_reset_pass));
                if (mFormAll.validate()) {
                    if (!mProgressDialog.isShowing()) {
                        mProgressDialog.show();
                        msVerify = metVerify.getText().toString();
                        msPass = metPass.getText().toString();
                        msConfirmPass = metConfirmPass.getText().toString();
                        BmobSMS.verifySmsCode(mContext, msPhone, msVerify, new VerifySMSCodeListener() {
                            @Override
                            public void done(BmobException ex) {
                                if (ex == null) {//短信验证码已验证成功
                                    resetPass();
                                } else {
                                    Toast.makeText(mContext, mContext.getResources().getString(R.string.vertify_fail), Toast.LENGTH_SHORT).show();

                                }
                                mProgressDialog.dismiss();
                            }
                        });
                    }
                }
                break;
            case R.id.id_top_back_tv:
            case R.id.id_top_back_iv_img:
                finish();
                break;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        BmobSMS.initialize(this, "253950cf6fe3d5d8682b0f4e0251ef63");
        initData();
    }

    private void sendVertify() {
        BmobSMS.requestSMSCode(mContext, msPhone, "笑递短信模板", new RequestSMSCodeListener() {
            @Override
            public void done(Integer smsId, BmobException ex) {
                if (ex == null) {//验证码发送成功
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.vertify_have_send), Toast.LENGTH_SHORT).show();
                } else {
                }
            }
        });
    }

    private void isRegister(String msPhone) {
        String sRegister = XDApplication.dbUrl + "/user/phone/exist?phone=" + msPhone;
        RequestParams isRegister = new RequestParams(sRegister);
        x.http().get(isRegister, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JSONObject json = (JSONObject) JSON.parse(result);
                String s = json.getString("exist");
                if (s.equals("false")) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.account_have_not_register), Toast.LENGTH_SHORT).show();
                } else if (s.equals("true")) {
                    sendVertify();
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
                Errorutils.showXutilError(mContext, ex);
                Errorutils.showError(mContext, ex,"isRegister","ForgetPasswordActivity",null);
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * 重设密码(未完善，缺少接口)
     */
    private void resetPass() {
        if (!msPass.equals(msConfirmPass)) {
            Toast.makeText(mContext, getResources().getString(R.string.two_pass_different), Toast.LENGTH_SHORT).show();
        } else {
            String url = XDApplication.dbUrl+"/user/password";
            RequestParams params = new RequestParams(url);
            params.addBodyParameter("username",mUser.getmUsername());
            params.addBodyParameter("token",mUser.getmToken());
            params.addBodyParameter("password",msConfirmPass);
            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    JSONObject json = (JSONObject) JSON.parse(result);
                    String status = json.getString("status");
                    if(status.equals("success")){
                        Toast.makeText(mContext, "修改密码成功", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Toast.makeText(mContext, "修改密码失败", Toast.LENGTH_SHORT).show();
                    Errorutils.showXutilError(mContext, ex);
                    Errorutils.showError(mContext, ex,"resetPass","ForgetPasswordActivity",null);
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

    private void initData() {
        /**
         * 初始化控件
         */
        mContext = ForgetPasswordActivity.this;
        mtvTopBack.setText(getResources().getString(R.string.login));
        mtvTopBack.setTextColor(Color.WHITE);
        mtvTopContent.setText(getResources().getString(R.string.forget_password));
        mivTopBack.setImageResource(R.drawable.go_back_white);
        mUser = XDApplication.getmUser();
        /**
         * 表单验证
         */
        mFormAll = new Form();
        mFormPhone = new Form();
        //验证电话号码
        Validate validate_phone = new Validate(metPhone);
        validate_phone.addValidator(new NotEmptyValidator(this));
        validate_phone.addValidator(new PhoneValidator(this));
        mFormPhone.addValidates(validate_phone);
        mFormAll.addValidates(validate_phone);
        //验证验证码
        Validate validate_verify = new Validate(metVerify);
        validate_verify.addValidator(new NotEmptyValidator(this));
        mFormAll.addValidates(validate_verify);
        //重设密码
        Validate validate_reset_pass = new Validate(metPass);
        validate_reset_pass.addValidator(new NotEmptyValidator(this));
        mFormAll.addValidates(validate_reset_pass);
        //确认密码
        Validate validate_confirm_pass = new Validate(metConfirmPass);
        validate_confirm_pass.addValidator(new NotEmptyValidator(this));
        mFormAll.addValidates(validate_confirm_pass);

        /**
         * 初始化dialog
         */
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }
}
