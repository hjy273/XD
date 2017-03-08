package com.tyb.xd.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.throrinstudio.android.common.libs.validator.Form;
import com.throrinstudio.android.common.libs.validator.Validate;
import com.throrinstudio.android.common.libs.validator.validator.NotEmptyValidator;
import com.tyb.xd.R;
import com.tyb.xd.bean.ThirdPartyUser;
import com.tyb.xd.utils.Errorutils;
import com.tyb.xd.utils.SharePreferenceUtils;
import com.tyb.xd.utils.Util;
import com.tyb.xd.view.SchoolPicker;


import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import butterknife.BindView;

/**
 * 实名认证
 */
@ContentView(R.layout.ac_status_identificate)
public class RealNameVertifyActivity extends CheckPermissionsActivity {
    @ViewInject(R.id.id_top_tv_content)
    private TextView mtvTopContent;
    @ViewInject(R.id.id_top_back_iv_img)
    private ImageView mivTopBack;
    @ViewInject(R.id.id_top_rl)
    private RelativeLayout mrlTop;
    @ViewInject(R.id.id_ac_statu_identificate_tv_school)
    private TextView mtvSchool;
    @ViewInject(R.id.id_ac_statu_identificate_et_school_number)
    private EditText metStuNum;
    @ViewInject(R.id.id_ac_statu_identificate_et_school_pass)
    private EditText metStuPass;
    @ViewInject(R.id.id_ac_forget_pass_btn_confirm)
    private Button mbtnConfirm;
    @ViewInject(R.id.id_ac_statu_identificate_et_name)
    private EditText metName;
    @ViewInject(R.id.id_ac_statu_identificate_tv_role)
    private TextView mtvRole;
    private String[] permission = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
    };

    //学号
    private String msUserNum;
    //密码
    private String msUserPass;
    //真实姓名
    private String msUserName;
    //用户角色
    private String msUserRole;
    //学校+校区
    private String[] mSchool;

    private Context mContext;

    private ProgressDialog mProgressDialog;

    private PopupWindow mPopSelectSchool;

    Form mForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        bundle.putStringArray("permission", permission);
        super.onCreate(bundle);
        x.view().inject(this);
        initView();
        initData();
    }

    /**
     * 初始化数据
     * 包括表单+提示dialpg
     */
    private void initData() {
        mForm = new Form();
        Validate validate_number = new Validate(metStuNum);
        validate_number.addValidator(new NotEmptyValidator(this));
        Validate validate_pass = new Validate(metStuPass);
        validate_pass.addValidator(new NotEmptyValidator(this));
        Validate validate_name = new Validate(metName);
        validate_name.addValidator(new NotEmptyValidator(this));
        mForm.addValidates(validate_number);
        mForm.addValidates(validate_pass);
        mForm.addValidates(validate_name);
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle(mContext.getResources().getString(R.string.identificating));
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    private void initView() {
        mContext = RealNameVertifyActivity.this;
        mtvTopContent.setText(mContext.getResources().getString(R.string.real_name_identificate));
        mivTopBack.setImageResource(R.drawable.go_back_white);
    }

    @Event(value = {R.id.id_top_back_iv_img, R.id.id_ac_statu_identificate_tv_school, R.id.id_ac_forget_pass_btn_confirm, R.id.id_ac_statu_identificate_tv_role})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_ac_statu_identificate_tv_school:
                showPopWindowSelectSchool();
                break;
            case R.id.id_ac_forget_pass_btn_confirm:
                if (mForm.validate()) {
                    msUserNum = metStuNum.getText().toString();
                    msUserPass = metStuPass.getText().toString();
                    msUserRole = mtvRole.getText().toString();
                    /**
                     * 条件判断，返回需要的角色字符串
                     */
                    msUserRole = (msUserRole.equals(mContext.getResources().getString(R.string.student))) ?
                            "student" : "teacher";
                    msUserName = metName.getText().toString();
                    String schoolAll = mtvSchool.getText().toString();
                    mSchool = schoolAll.split("-");
                    mProgressDialog.show();
                    vertify();
                }
                break;
            case R.id.id_ac_statu_identificate_tv_role:
                msUserRole = mtvRole.getText().toString();
                msUserRole = msUserRole.equals(mContext.getResources().getString(R.string.student)) ?
                        mContext.getResources().getString(R.string.teacher) :
                        mContext.getResources().getString(R.string.student);
                mtvRole.setText(msUserRole);
                break;
            case R.id.id_top_back_iv_img:
                finish();
                break;
        }
    }

    private void showPopWindowSelectSchool() {
        if (mPopSelectSchool == null) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.pop_select_school, null);
            mPopSelectSchool = new PopupWindow(view, Util.dpToPx(getResources(), 320),
                    LinearLayout.LayoutParams.WRAP_CONTENT, true);
            initPopWindowSelectSchool(view);
        }
        mPopSelectSchool.showAtLocation(mtvSchool.getRootView(), Gravity.CENTER, 0, 0);
    }

    private void initPopWindowSelectSchool(View view) {
        final SchoolPicker schoolPicker = (SchoolPicker) view.findViewById(R.id.id_pop_select_place_tp_school);
        Button btnCancel = (Button) view.findViewById(R.id.id_pop_select_place_cancel);
        Button btnConfirm = (Button) view.findViewById(R.id.id_pop_select_place_confirm);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopSelectSchool.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtvSchool.setText(schoolPicker.toString());
                mPopSelectSchool.dismiss();
            }
        });
        mPopSelectSchool.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mPopSelectSchool.setOutsideTouchable(true);
    }

    /**
     * 发起http请求验证
     */
    private void vertify() {
        String url = XDApplication.dbUrl + "/user/school/auth";
        RequestParams vertify = new RequestParams(url);
        vertify.addBodyParameter("truename", msUserName);
        vertify.addBodyParameter("school", mSchool[0]);
        vertify.addBodyParameter("campus", mSchool[1]);
        vertify.addBodyParameter("role", msUserRole);
        vertify.addBodyParameter("school_id", msUserNum);
        vertify.addBodyParameter("password", msUserPass);
        vertify.addBodyParameter("token", XDApplication.getmUser().getmToken());
        vertify.addBodyParameter("username", XDApplication.getmUser().getmUsername());
        x.http().post(vertify, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                mProgressDialog.dismiss();
                JSONObject json = JSON.parseObject(result);
                String status = json.getString("status");
                Toast.makeText(mContext, (status.equals("success") ?
                        mContext.getResources().getString(R.string.identificate_success) :
                        mContext.getResources().getString(R.string.identificate_fail)), Toast.LENGTH_SHORT).show();
                if (status.equals("success")) {
                    XDApplication.getmUser().setmIsIdentificate(true);
                    SharePreferenceUtils.setCurrUser(mContext, XDApplication.getmUser());
                    if (XDApplication.getmUser() instanceof ThirdPartyUser) {
                        WelcomeActivity.complete_Info_Third(mContext);
                    } else {
                        WelcomeActivity.complete_Info(mContext);
                    }
                    finish();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mProgressDialog.dismiss();
                //显示错误信息
                Errorutils.showXutilError(mContext, ex);
                Errorutils.showError(mContext, ex, "vertify", "RealNameVertifyActivity", RealNameVertifyActivity.this);
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
