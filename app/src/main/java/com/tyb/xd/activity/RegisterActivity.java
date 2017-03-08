package com.tyb.xd.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.throrinstudio.android.common.libs.validator.Form;
import com.throrinstudio.android.common.libs.validator.Validate;
import com.throrinstudio.android.common.libs.validator.validator.NotEmptyValidator;
import com.throrinstudio.android.common.libs.validator.validator.PhoneValidator;
import com.tyb.xd.R;
import com.tyb.xd.utils.Errorutils;
import com.tyb.xd.utils.FileUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import c.b.BP;
import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;

@ContentView(R.layout.ac_register)
public class RegisterActivity extends CheckPermissionsActivity {
    @ViewInject(R.id.id_ac_register_iv_pwd_display)
    private ImageView mivDisplsy;
    @ViewInject(R.id.id_top_back_iv_img)
    ImageView mivBack;
    @ViewInject(R.id.id_top_back_tv)
    TextView mtvBack;
    @ViewInject(R.id.id_top_tv_content)
    TextView mtvContent;
    @ViewInject(R.id.id_top_rl)
    private RelativeLayout mrlTop;
    @ViewInject(R.id.id_ac_register_et_inviteCode)
    private TextView metInviteCode;
    @ViewInject(R.id.id_ac_register_iv_img)
    ImageView mivSetHead;
    @ViewInject(R.id.id_ac_register_et_phone)
    EditText metPhone;
    @ViewInject(R.id.id_ac_register_et_vertify)
    EditText metVertify;
    @ViewInject(R.id.id_ac_register_btn_get_vertify)
    Button mbtnGetVertify;
    @ViewInject(R.id.id_ac_register_et_nickname)
    EditText metNickname;
    @ViewInject(R.id.id_ac_register_et_pass)
    EditText metPass;
    @ViewInject(R.id.id_ac_register_btn_confirm)
    Button mbtnConfirm;
    private PopupWindow mPopWindow;
    private LayoutInflater mLayoutInflater;

    //图片保存的文件夹
    private static String PHOTOSAVEPATH = XDApplication.msSavePhth + "/crop_photo/";

    //以当前时间的毫秒数当做文件名，设置好的图片的路径
    private String photoname = System.currentTimeMillis() + ".png";
    private String mPath;  //要找的图片路径

    private final static int PHOTOBYGALLERY = 0;//从相册获取照片

    private final static int PHOTOTACK = 1;//拍照获取

    private final static int PHOTOCOMPLETEBYTAKE = 2;//完成
    private final static int PHOTOCOMPLETEBYGALLERY = 3;//完成

    private static int PHOTOCROP = 3;//图片裁剪


    /**
     * popupwindow里的控件
     */
    private TextView mTxtGallery;

    private TextView mTxtTack;

    private TextView mCancel;

    private Context mContext;

    /**
     * 短信验证
     */
    Form mFormPhone;
    Form mFormAll;
    private Boolean mIsRegister = false;
    /**
     * 密码是否显示
     */
    private Boolean mdisplayPwd = false;
    private String msPhone;
    private String msVerify;
    private String msNickname;
    private String msUsername;
    private String msPass;
    private String msOpenid = "";
    private String msInviteCode;

    private String[] permissionarray = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_PHONE_STATE,
    };


    private ProgressDialog progressDialog;


    @Event(value = {R.id.id_ac_register_iv_pwd_display, R.id.id_top_back_tv, R.id.id_ac_register_btn_confirm, R.id.id_ac_register_btn_get_vertify, R.id.id_ac_register_iv_img, R.id.id_top_back_iv_img}, type = View.OnClickListener.class)
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_ac_register_btn_get_vertify:
                progressDialog.setTitle(mContext.getResources().getString(R.string.is_getting_vertify));
                if (mFormPhone.validate()) {
                    if (!progressDialog.isShowing())
                        progressDialog.show();
                    msPhone = metPhone.getText().toString();
                    isRegister(msPhone);
                }
                break;
            case R.id.id_ac_register_btn_confirm:
                progressDialog.setTitle(mContext.getResources().getString(R.string.is_registering));
                if (mFormAll.validate()) {
                    if (!progressDialog.isShowing())
                        progressDialog.show();
                    msVerify = metVertify.getText().toString();
                    msInviteCode = metInviteCode.getText().toString();
                    msNickname = metNickname.getText().toString();
                    msPass = metPass.getText().toString();
                    msPhone = metPhone.getText().toString();
                    if (!verifyUsername(msUsername)) {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.username_is_illage), Toast.LENGTH_SHORT).show();
                    } else {
                        BmobSMS.verifySmsCode(mContext, msPhone, msVerify, new VerifySMSCodeListener() {
                            @Override
                            public void done(BmobException ex) {
                                // TODO Auto-generated method stub
                                if (ex == null) {//短信验证码已验证成功
                                    register();
                                } else {
                                    Toast.makeText(mContext, mContext.getResources().getString(R.string.vertify_fail), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }
                }
                break;
            case R.id.id_ac_register_iv_img:
                showPopupWindow(mivSetHead);
                break;
            case R.id.id_top_back_iv_img:
            case R.id.id_top_back_tv:
                finish();
                break;
            case R.id.id_ac_register_iv_pwd_display:
                if (!mdisplayPwd) {
                    mdisplayPwd = true;
                    mivDisplsy.setImageResource(R.drawable.eye);
                    metPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mdisplayPwd = false;
                    mivDisplsy.setImageResource(R.drawable.eye_close);
                    metPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
        }
    }

    /**
     * 验证用户名格式是否符合要求
     *
     * @param msUsername
     * @return
     */
    private boolean verifyUsername(String msUsername) {
        String p = "^[a-zA-Z0-9][a-zA-Z0-9_]{1,14}[a-zA-Z0-9]$";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(msUsername);
        return matcher.find();
    }

    private void sendVerify() {
        BmobSMS.requestSMSCode(mContext, msPhone, "笑递短信模板", new RequestSMSCodeListener() {
            @Override
            public void done(Integer smsId, BmobException ex) {
                if (ex == null) {//验证码发送成功
                    Toast.makeText(mContext, getResources().getString(R.string.vertify_have_send), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.verify_send_fail), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
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
                    sendVerify();
                } else if (s.equals("true")) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.account_have_register), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
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
    }

    /**
     * 用户进行注册
     */
    private void register() {
        String sRegister = XDApplication.dbUrl + "/user/register";
        final RequestParams register = new RequestParams(sRegister);
        register.addBodyParameter("nickname", msNickname);
        register.addBodyParameter("username", msUsername);
        register.addBodyParameter("phone", msPhone);
        register.addBodyParameter("password", msPass);
        register.addBodyParameter("code", msInviteCode);
        register.addBodyParameter("unique_id", msOpenid);
        if (mPath != null && !mPath.startsWith("http")) {
            register.addBodyParameter("headimg", new File(mPath));
        } else {
            if (mPath != null) {
                //存储图片到本地
                RequestParams imgPar = new RequestParams(mPath);
                x.http().get(imgPar, new Callback.CommonCallback<File>() {
                    @Override
                    public void onSuccess(File result) {
                        FileUtil.writeFile(mContext, msPhone, result, msPhone, FileUtil.FILE_IMAGE);
                        String path = mContext.getExternalFilesDir(null).getAbsolutePath() + "/" + msPhone + "/" + msPhone + ".png";
                        XDApplication.getmUser().setmUserPath(path);
                        mPath = path;
                        register.addBodyParameter("headimg", new File(path));
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
            }
        }
        x.http().post(register, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                progressDialog.dismiss();
                Toast.makeText(mContext, mContext.getResources().getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                XDApplication.getmUser().setmUserPhone(msPhone);
                XDApplication.getmUser().setmUserPass(msPass);
                XDApplication.getmUser().setmUsername(msUsername);
                JSONObject jsonObject = JSON.parseObject(result);
                XDApplication.getmUser().setmToken(jsonObject.getString("token"));
                /**
                 * 判断是否需要复制文件
                 */
                if (mPath != null && !TextUtils.isEmpty(mPath)) {
                    copyImgFile();
                }
                toIdentificate();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                progressDialog.dismiss();
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
    }

    /**
     * 跳转进行实名认证
     */
    private void toIdentificate() {
        Intent intent = new Intent(mContext, RealNameVertifyActivity.class);
        startActivity(intent);
        finish();
    }

    private void copyImgFile() {
        File file = new File(XDApplication.msSavePhth, msPhone);
        if (!file.exists()) {
            file.mkdir();
        }
        File filecopy = new File(file.getAbsolutePath() + "/" + msPhone + ".png");
        FileUtil.copyFile(mPath, filecopy.getAbsolutePath());
        File filedelete = new File(mPath);
        filedelete.delete();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        Bundle bundle_permission = new Bundle();
        bundle_permission.putStringArray("permission", permissionarray);
        super.onCreate(bundle_permission);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);
        BmobSMS.initialize(this, "253950cf6fe3d5d8682b0f4e0251ef63");
        initData();
    }

    private void initData() {
        mContext = RegisterActivity.this;
        mtvBack.setText(getResources().getString(R.string.login));
        Intent intent = getIntent();
        int type = intent.getIntExtra("type", 0);
        if (type == LoginActivity.OTHER_LOGIN) {
            mtvContent.setText(getResources().getString(R.string.completeInfo));
            mPath = intent.getStringExtra("headimg");
            String username = intent.getStringExtra("username");
            metNickname.setText(username);
            ImageOptions options = new ImageOptions.Builder()
                    .setFailureDrawableId(R.drawable.login_logo)
                    .setLoadingDrawableId(R.drawable.login_logo)
                    .setCircular(true).build();
            x.image().bind(mivSetHead, mPath, options);
            msOpenid = intent.getStringExtra("openid");
        } else {
            mtvContent.setText(getResources().getString(R.string.register));
        }
        mtvBack.setTextColor(Color.WHITE);
        mivBack.setImageResource(R.drawable.go_back_white);
        mLayoutInflater = LayoutInflater.from(this);
        File file = new File(XDApplication.msSavePhth, "crop_photo");
        if (!file.exists()) {
            file.mkdir();
        }
        /**
         * 短信验证
         */
        mFormPhone = new Form();
        mFormAll = new Form();
        Validate validate_phone = new Validate(metPhone);
        validate_phone.addValidator(new NotEmptyValidator(this));
        validate_phone.addValidator(new PhoneValidator(this));
        mFormPhone.addValidates(validate_phone);
        mFormAll.addValidates(validate_phone);
        Validate validate_vertify = new Validate(metVertify);
        validate_vertify.addValidator(new NotEmptyValidator(this));
        mFormAll.addValidates(validate_vertify);
        Validate validate_name = new Validate(metNickname);
        validate_name.addValidator(new NotEmptyValidator(this));
        mFormAll.addValidates(validate_name);
        Validate validate_pass = new Validate(metPass);
        validate_pass.addValidator(new NotEmptyValidator(this));
        mFormAll.addValidates(validate_pass);
        /**
         * 初始化dialog
         */
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle(mContext.getResources().getString(R.string.is_registering));
        progressDialog.setCanceledOnTouchOutside(false);

    }

    private void showPopupWindow(ImageView img) {
        if (mPopWindow == null) {
            View view = mLayoutInflater.inflate(R.layout.pop_select_photo, null);
            mPopWindow = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT, true);
            initPopupWindow(view);
        }
        //设置位置
        mPopWindow.showAtLocation(img, Gravity.CENTER, 0, 0);
    }

    private void initPopupWindow(View v) {
        //获取控件
        mTxtGallery = (TextView) v.findViewById(R.id.id_pop_select_photo_tv_from_gallery);
        mTxtTack = (TextView) v.findViewById(R.id.id_pop_select_photo_tv_take_photo);
        mCancel = (TextView) v.findViewById(R.id.id_pop_select_photo_tv_cancel);
        mTxtGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
                startToGetPhotoByGallery();
            }
        });
        mTxtTack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
                startToGetPhotoByTack();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopWindow.isShowing())
                    mPopWindow.dismiss();
            }
        });
        //设置动画
        mPopWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        //设置可以点击外面
        mPopWindow.setOutsideTouchable(true);
        //设置popupwindow为透明的，这样背景就是主界面的内容
        mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void startToGetPhotoByGallery() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openGalleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(openGalleryIntent, PHOTOBYGALLERY);
    }

    private void startToGetPhotoByTack() {
        photoname = String.valueOf(System.currentTimeMillis()) + ".png";
        Uri imageUri = null;
        /***********************************************************************************/
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = Uri.fromFile(new File(PHOTOSAVEPATH, photoname));
        openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, PHOTOTACK);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        Uri uri = null;
        switch (requestCode) {
            case PHOTOBYGALLERY:
                uri = data.getData();
                if (uri != null) {
                    if (Build.VERSION.SDK_INT > 18) {
                        if (DocumentsContract.isDocumentUri(mContext, uri)) {
                            String wholeID = DocumentsContract.getDocumentId(uri);
                            String id = wholeID.split(":")[1];
                            String[] column = {MediaStore.Images.Media.DATA};
                            String sel = MediaStore.Images.Media._ID + "=?";
                            Cursor cursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
                                    sel, new String[]{id}, null);
                            int columnIndex = cursor.getColumnIndex(column[0]);
                            if (cursor.moveToFirst()) {
                                mPath = cursor.getString(columnIndex);
                            }
                            cursor.close();
                        } else {
                            String[] projection = {MediaStore.Images.Media.DATA};
                            Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            cursor.moveToFirst();
                            mPath = cursor.getString(column_index);
                        }
                    } else {
                        String[] projection = {MediaStore.Images.Media.DATA};
                        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        mPath = cursor.getString(column_index);
                    }
                }
                /**
                 * 获取到照片之后调用裁剪acticity
                 */
                Intent intentGalley = new Intent(mContext, ClipActivity.class);
                intentGalley.putExtra("path", mPath);
                startActivityForResult(intentGalley, PHOTOCOMPLETEBYGALLERY);
                break;
            case PHOTOTACK:
                mPath = PHOTOSAVEPATH + photoname;
                //   uri = Uri.fromFile(new File(path));
                /**
                 * 拿到uri后进行裁剪处理
                 */
                Intent intentTake = new Intent(mContext, ClipActivity.class);
                intentTake.putExtra("path", mPath);
                startActivityForResult(intentTake, PHOTOCOMPLETEBYTAKE);
                break;
            case PHOTOCOMPLETEBYTAKE:
                final String temppath = data.getStringExtra("path");
                mivSetHead.setImageBitmap(FileUtil.getBitmapFormPath(mContext, temppath));
                /**
                 * 删除旧文件
                 */
                File file = new File(mPath);
                file.delete();
                mPath = temppath;
                break;
            case PHOTOCOMPLETEBYGALLERY:
                final String temppathgallery = data.getStringExtra("path");
                mivSetHead.setImageBitmap(FileUtil.getBitmapFormPath(mContext, temppathgallery));
                mPath = temppathgallery;
                break;
        }
    }
}
