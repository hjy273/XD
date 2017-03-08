package com.tyb.xd.fragment;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tyb.xd.R;
import com.tyb.xd.activity.CarryRecodeActivity;
import com.tyb.xd.activity.ClipActivity;
import com.tyb.xd.activity.LoginActivity;
import com.tyb.xd.activity.SettingActivity;
import com.tyb.xd.activity.SignActivity;
import com.tyb.xd.activity.WalletActivity;
import com.tyb.xd.activity.XDApplication;
import com.tyb.xd.activity.XiaodiAttestationActivity;
import com.tyb.xd.bean.User;
import com.tyb.xd.utils.Errorutils;
import com.tyb.xd.utils.FileUtil;
import com.tyb.xd.utils.SharePreferenceUtils;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

@ContentView(R.layout.fg_my)
public class FgMy extends Fragment {
    @ViewInject(R.id.id_top_tv_content)
    TextView mtvTopContent;
    @ViewInject(R.id.id_fg_my_me_iv_img)
    ImageView mivMyImg;
    @ViewInject(R.id.id_ac_my_me_tv_name)
    TextView mtvMyName;
    @ViewInject(R.id.id_my_rl_everyday_sign)
    RelativeLayout mrlEverydaySginIn;
    @ViewInject(R.id.id_my_tv_wallet)
    TextView mtvMyWallet;
    @ViewInject(R.id.id_my_rl_wallet)
    RelativeLayout mrlMyWallet;
    @ViewInject(R.id.id_ac_my_rl_carryrecode)
    RelativeLayout mrlMyCarryRecode;
    @ViewInject(R.id.id_ac_my_rl_setting)
    RelativeLayout mrlMySetting;
    @ViewInject(R.id.id_fg_my_tv_is_xiaodi_attestation)
    TextView mtvIsAttestation;
    @ViewInject(R.id.id_fg_my_rl_xiaodi_attestation)
    RelativeLayout mrlXiaoDiAttestation;
    @ViewInject(R.id.id_ac_my_tv_realName)
    private TextView mtvRealName;
    @ViewInject(R.id.id_fg_my_me_iv_setimg)
    private ImageView mivSetImg;
    private User user;

    private Context mContext;

    private static final int COMPLETE = 0x110;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case COMPLETE:
                    mProgressDialog.dismiss();
                    break;
            }
        }
    };


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

    private PopupWindow mPopWindow2;
    private PopupWindow mPopWindow;
    private TextView mTxtGallery;
    private TextView mTxtTack;
    private TextView mCancel;
    private TextView mEtName;

    private ProgressDialog mProgressDialog;

    @Event(value = {R.id.id_my_rl_everyday_sign, R.id.id_fg_my_me_iv_img, R.id.id_my_rl_wallet,
            R.id.id_ac_my_rl_carryrecode, R.id.id_ac_my_rl_setting,
            R.id.id_ac_my_me_tv_name,R.id.id_fg_my_rl_xiaodi_attestation}, type = View.OnClickListener.class)
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_my_rl_everyday_sign:
                toNext(SignActivity.class);
                break;
            case R.id.id_my_rl_wallet:
                toNext(WalletActivity.class);
                break;
            case R.id.id_ac_my_rl_carryrecode:
                toNext(CarryRecodeActivity.class);
                break;
            case R.id.id_ac_my_rl_setting:
                toNext(SettingActivity.class);
                break;
            case R.id.id_fg_my_me_iv_img:
            case R.id.id_fg_my_me_iv_setimg:
                if (SharePreferenceUtils.getLoginStatus(mContext)) {
                    showPopupWindow(mivSetImg);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.no_login), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }

                break;
            case R.id.id_ac_my_me_tv_name:
                String statu = mtvMyName.getText().toString();
                if (statu.equals(mContext.getResources().getString(R.string.login_register))) {
                    toNext(LoginActivity.class);
                } else {
                    showModifyPopupWindow(mtvMyName);
                }
                break;
            case R.id.id_fg_my_rl_xiaodi_attestation:
                if (!SharePreferenceUtils.getLoginStatus(mContext)) {
                    toNext(LoginActivity.class);
                }else{
                    if(XDApplication.getmUser().getmRole().equals(XDApplication.ROLE_FULLTIME))
                    {//说明是笑递员
                        Toast.makeText(mContext,getResources().getString(R.string.you_are_xiaodi),Toast.LENGTH_SHORT).show();
                    }else{
                        //进行笑递员认证
                        toNext(XiaodiAttestationActivity.class);
                    }
                }
                break;

        }
    }

    private void showModifyPopupWindow(TextView mtvMyName) {
        if (mPopWindow2 == null) {
            View mView = LayoutInflater.from(mContext).inflate(R.layout.pop_modifyname, null);
            mPopWindow2 = new PopupWindow(mView, RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT, true);
            initModifyPopupWindow(mView);
        }
        mPopWindow2.showAtLocation(mtvMyName, Gravity.CENTER, 0, 0);
    }

    private void initModifyPopupWindow(View v) {
        mEtName = (TextView) v.findViewById(R.id.id_pop_modifyname_et_nickname);
        TextView mBtnConfirm = (Button) v.findViewById(R.id.id_pop_modifyname_btn_confirm);
        TextView mBtnCancel = (Button) v.findViewById(R.id.id_pop_modifyname_btn_cancel);
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newNickname = mEtName.getText().toString();
                if (newNickname.equals("")) {
                    Toast.makeText(mContext, "昵称不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (SharePreferenceUtils.getLoginStatus(mContext)) {
                        String url = XDApplication.dbUrl + "/user/self";
                        RequestParams reset = new RequestParams(url);
                        reset.addBodyParameter("nickname", newNickname);
                        reset.addBodyParameter("token", XDApplication.getmUser().getmToken());
                        reset.addBodyParameter("username", XDApplication.getmUser().getmUsername());
                        x.http().request(HttpMethod.PUT, reset, new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                JSONObject json = JSON.parseObject(result);
                                String status = json.getString("status");
                                if (status.equals("success")) {
                                    mtvMyName.setText(newNickname);
                                    Toast.makeText(mContext, "昵称更改成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, "昵称更改失败", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable ex, boolean isOnCallback) {
                                Errorutils.showXutilError(mContext, ex);
                                Errorutils.showError(mContext, ex, "initPopWindow2", "FgMy", FgMy.this);
                            }

                            @Override
                            public void onCancelled(CancelledException cex) {

                            }

                            @Override
                            public void onFinished() {

                            }
                        });
                        mPopWindow2.dismiss();
                    } else {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.no_login), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow2.dismiss();
            }
        });
    }

    private void showPopupWindow(ImageView mivSetImg) {
        if (mPopWindow == null) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.pop_select_photo, null);
            mPopWindow = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT, true);
            initPopupWindow(view);
        }
        //设置位置
        mPopWindow.showAtLocation(mivSetImg, Gravity.CENTER, 0, 0);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != getActivity().RESULT_OK) {
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
                mivMyImg.setImageBitmap(FileUtil.getBitmapFormPath(mContext, temppath));
                /**
                 * 删除旧文件
                 */
                File file = new File(mPath);
                file.delete();
                mPath = temppath;
                /**
                 * 执行上传照片
                 */
                uploadImg();
                break;
            case PHOTOCOMPLETEBYGALLERY:
                final String temppathgallery = data.getStringExtra("path");
                mivMyImg.setImageBitmap(FileUtil.getBitmapFormPath(mContext, temppathgallery));
                mPath = temppathgallery;
                /**
                 * 执行上传照片
                 */
                uploadImg();
                break;
        }
    }

    private void uploadImg() {
        if (mPath != null && SharePreferenceUtils.getLoginStatus(mContext)) {
            /**
             * 将原来的照片进行删除
             */
            File file = new File(XDApplication.getmUser().getmUserPath());
            if (file.exists()) {
                file.delete();
            }
            FileUtil.copyFile(mPath, XDApplication.getmUser().getmUserPath());
            /**
             * 上传照片
             */
            String url = XDApplication.dbUrl + "/user/self";
            RequestParams requestParams = new RequestParams(url);
            requestParams.addBodyParameter("headimg", new File(mPath));
            requestParams.addBodyParameter("token", XDApplication.getmUser().getmToken());
            requestParams.addBodyParameter("username", XDApplication.getmUser().getmUsername());
            x.http().request(HttpMethod.PUT, requestParams, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    JSONObject jsonObject = JSON.parseObject(result);
                    if (jsonObject.getString("status").equals("success")) {
                    } else {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.upload_fail), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Errorutils.showXutilError(mContext, ex);
                    Errorutils.showError(mContext, ex, "uploadImg", "FgMy", FgMy.this);
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

    public FgMy() {
    }

    public static FgMy newInstance() {
        FgMy fragment = new FgMy();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = x.view().inject(this, inflater, container);
        mtvTopContent.setText(getResources().getString(R.string.me));
        return view;
    }

    public void initView() {
        mContext = getActivity();
        user = XDApplication.getmUser();
        //设置是否认证
        if (user.ismIsIdentificate()) {
            mtvRealName.setText(getResources().getString(R.string.have_real_name_identificate));
        }
        //设置用户总笑点
        String stotalMoney = (user.getmSilverMoney() + user.getmGoldMoney()) + "";
        mtvMyWallet.setText(stotalMoney);
        //设置用户名
        if (!SharePreferenceUtils.getLoginStatus(mContext)) {
            mtvMyName.setText(mContext.getResources().getString(R.string.login_register));
            mivMyImg.setImageResource(R.drawable.default_headimg);
        } else {
            mtvMyName.setText(user.getmNickname());
            if(XDApplication.getmUser().getmRole().equals(XDApplication.ROLE_FULLTIME))
            {//已经进行笑递员认证
                mtvIsAttestation.setText(getResources().getString(R.string.xiaodi_attestation_yes));
            }else{//笑递员未认证
                mtvIsAttestation.setText(getResources().getString(R.string.xiaodi_attestation_not));
            }
            //设置用户图片
            final String imgPath = user.getmUserPath();
            //图片不为空直接设置
            if (!TextUtils.isEmpty(imgPath) && (new File(imgPath).exists())) {
                Bitmap bm = BitmapFactory.decodeFile(imgPath);
                mivMyImg.setImageBitmap(bm);
            } else if ((!TextUtils.isEmpty(user.getmToken())) && (!TextUtils.isEmpty(user.getmUsername()))) {//图片为空则根据username和token去网络获取
                String url = XDApplication.dbUrl + "/user/self";
                RequestParams params = new RequestParams(url);
                params.addBodyParameter("token", user.getmToken());
                params.addBodyParameter("username", user.getmUsername());
                x.http().get(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject json = JSON.parseObject(result);
                        String status = json.getString("status");
                        if (status.equals("success")) {
                            JSONObject user_json = json.getJSONObject("user");
                            String imgUrl = user_json.getString("headimg");
                            ImageOptions options = new ImageOptions.Builder()
                                    .setFailureDrawableId(R.drawable.default_headimg)
                                    .setLoadingDrawableId(R.drawable.default_headimg)
                                    .setCircular(true).build();
                            x.image().bind(mivMyImg, imgUrl, options);
                            //存储图片到本地
                            RequestParams imgPar = new RequestParams(imgUrl);
                            x.http().get(imgPar, new CommonCallback<File>() {
                                @Override
                                public void onSuccess(File result) {
                                    FileUtil.writeFile(mContext, user.getmUserPhone(), result, user.getmUserPhone(), FileUtil.FILE_IMAGE);
                                }

                                @Override
                                public void onError(Throwable ex, boolean isOnCallback) {
                                    Errorutils.showXutilError(mContext, ex);
                                    Errorutils.showError(mContext, ex, "initView", "FgMy", FgMy.this);
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

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Errorutils.showXutilError(mContext, ex);
                        Errorutils.showError(mContext, ex, "initView", "FgMy", FgMy.this);
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
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(mContext.getResources().getString(R.string.uploading));
    }

    private void toNext(Class<?> next) {
        if (SharePreferenceUtils.getLoginStatus(mContext)) {
            Intent intent = new Intent(mContext, next);
            startActivity(intent);
        } else {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.no_login), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    private void copyImgFile() {
        File file = new File(XDApplication.msSavePhth, XDApplication.getmUser().getmUserPhone());
        if (!file.exists()) {
            file.mkdir();
        }
        File filecopy = new File(file.getAbsolutePath() + "/" + XDApplication.getmUser().getmUserPhone() + ".png");
        FileUtil.copyFile(mPath, filecopy.getAbsolutePath());
        File filedelete = new File(mPath);
        filedelete.delete();
    }
}
