package com.tyb.xd.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.tyb.xd.bean.ThirdPartyUser;
import com.tyb.xd.bean.User;
import com.tyb.xd.utils.Errorutils;
import com.tyb.xd.utils.SharePreferenceUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * 登录界面
 */
@ContentView(R.layout.ac_login)
public class LoginActivity extends CheckPermissionsActivity{
    @ViewInject(R.id.id_ac_login_et_username)
    EditText metUserPhone;
    @ViewInject(R.id.id_ac_login_et_password)
    EditText metUserPass;
    @ViewInject(R.id.id_ac_login_btn_login)
    Button mbtnLogin;
    @ViewInject(R.id.id_ac_login_tv_forget_password)
    TextView mtvForgetPass;
    @ViewInject(R.id.id_ac_login_tv_registor)
    TextView mtvRegister;
    @ViewInject(R.id.id_ac_login_iv_wechat)
    ImageView mivWeChatLogin;
    @ViewInject(R.id.id_ac_login_iv_weibo)
    ImageView mivWeiBoLogin;
    @ViewInject(R.id.id_ac_login_iv_qq)
    ImageView mivQQLogin;

    private String msUserPhone;

    private String msUserPass;

    private User mUser;
    private String openid = "";
    private Context mContext;

    private Form mForm;

    private ProgressDialog mProgressDialog;
    //第三方登录
    public static int OTHER_LOGIN = 0x01;

    private String[] permissionarray = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_PHONE_STATE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        bundle.putStringArray("permission", permissionarray);
        super.onCreate(bundle);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        x.view().inject(this);
        initView();
        initData();
    }

    private void initData() {
        mForm = new Form();
        Validate validate_phone = new Validate(metUserPhone);
        validate_phone.addValidator(new NotEmptyValidator(this));
        validate_phone.addValidator(new PhoneValidator(this));

        Validate validate_pass = new Validate(metUserPass);
        validate_pass.addValidator(new NotEmptyValidator(this));
        mForm.addValidates(validate_pass);
        mForm.addValidates(validate_phone);
        /**
         * 初始化Dialog
         */
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle(mContext.getResources().getString(R.string.logining));
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    private void initView() {
        mContext = LoginActivity.this;
        mUser = SharePreferenceUtils.getCurrLoginUser(mContext);
        if (mUser != null) {
            if (!(mUser instanceof ThirdPartyUser)) {
                if ((!TextUtils.isEmpty(mUser.getmUserPhone())) &&
                        (!TextUtils.isEmpty(mUser.getmUserPass()))) {
                    metUserPhone.setText(mUser.getmUserPhone() + "");
                    metUserPass.setText(mUser.getmUserPass() + "");
                }
            }
        }
    }

    @Event(value = {R.id.id_ac_login_btn_login, R.id.id_ac_login_tv_forget_password, R.id.id_ac_login_tv_registor, R.id.id_ac_login_iv_wechat, R.id.id_ac_login_iv_weibo, R.id.id_ac_login_iv_qq}, type = View.OnClickListener.class)
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_ac_login_btn_login:
                if (mForm.validate()) {
                    msUserPhone = metUserPhone.getText().toString();
                    msUserPass = metUserPass.getText().toString();
                    User user = new User();
                    user.setmUserPhone(msUserPhone);
                    user.setmUserPass(msUserPass);
                    mProgressDialog.show();
                    login(user);
                }
                break;
            case R.id.id_ac_login_tv_forget_password:
                Intent intent_forgetpass = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent_forgetpass);
                break;
            case R.id.id_ac_login_tv_registor:
                Intent intent_register = new Intent(mContext, RegisterActivity.class);
                startActivity(intent_register);
                break;
            case R.id.id_ac_login_iv_wechat:
                Toast.makeText(mContext, "暂未开通", Toast.LENGTH_SHORT).show();
                break;
            case R.id.id_ac_login_iv_weibo:
                login(SinaWeibo.NAME);
                break;
            case R.id.id_ac_login_iv_qq:
                login(QQ.NAME);
                break;
        }
    }

    private void login(final String platformName) {
        mProgressDialog.setTitle("正在跳转...");
        mProgressDialog.show();
        ShareSDK.initSDK(mContext);
        final Platform plat = ShareSDK.getPlatform(this, platformName);
        final PlatformDb db = plat.getDb();
        if(plat.isValid()){
            plat.removeAccount();
        }
        plat.SSOSetting(false);
        plat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                mProgressDialog.dismiss();
                if (platformName.equals(QQ.NAME)) {
                    openid = "qq_" + plat.getDb().getUserId();
                } else if (platformName.equals(SinaWeibo.NAME)) {
                    openid = "weibo_" + plat.getDb().getUserId();
                } else {
                    openid = "weixin_" + plat.getDb().getUserId();
                }
                String url = XDApplication.dbUrl + "/user/uniqueid/" + openid;
                RequestParams params = new RequestParams(url);
                params.addBodyParameter("username", "admin1314");
                params.addBodyParameter("token", "$1$Ewpc17O/$hdkoVxGTQRzRMoKeDMMaF1");
                x.http().get(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject json = JSON.parseObject(result);
                        String status = json.getString("status");
                        if (status.equals("success")) {
                            XDApplication.setmUser(new ThirdPartyUser());
                            ((ThirdPartyUser)XDApplication.getmUser()).setmThirdPartyId(openid);
                            XDApplication.getmUser().setmToken(json.getString("token"));
                            XDApplication.getmUser().setmUsername(json.getJSONObject("user").getString("username"));
                            loginSuccess(XDApplication.getmUser());
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Intent intent = new Intent(mContext, RegisterActivity.class);
                        intent.putExtra("headimg", db.getUserIcon());
                        intent.putExtra("username", db.getUserName());
                        intent.putExtra("type", OTHER_LOGIN);
                        intent.putExtra("openid", openid);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                    }

                    @Override
                    public void onFinished() {
                    }
                });
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                mProgressDialog.dismiss();
            }
        });
        plat.authorize();
    }

    private void login(final User user) {
        String url = XDApplication.dbUrl + "/user/login";
        RequestParams login = new RequestParams(url);
        login.addBodyParameter("phone", msUserPhone);
        login.addBodyParameter("password", msUserPass);
        x.http().post(login, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                mProgressDialog.dismiss();
                JSONObject json = JSON.parseObject(result);
                String status = json.getString("status");
                if (status.equals("success")) {
                    user.setmToken(json.getString("token"));
                    user.setmUsername(json.getString("username"));
                    XDApplication.setmUser(user);
                    loginSuccess(XDApplication.getmUser());
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mProgressDialog.dismiss();
                Errorutils.showXutilError(mContext, ex);
                Errorutils.showError(mContext, ex,null,null,null);
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void loginSuccess(User user) {
        SharePreferenceUtils.setLoginStatu(mContext, true);
        if(XDApplication.getmUser() instanceof ThirdPartyUser)
        {
            WelcomeActivity.complete_Info_Third(mContext);
        }else{
            WelcomeActivity.complete_Info(mContext);
        }
        Intent intent = new Intent(mContext, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}