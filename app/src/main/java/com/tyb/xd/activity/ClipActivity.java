package com.tyb.xd.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tyb.xd.R;
import com.tyb.xd.utils.clip_image.ClipImageLayout;
import com.tyb.xd.utils.clip_image.ImageTools;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;

import butterknife.BindView;

/**
 */
@ContentView(R.layout.ac_clipimage)
public class ClipActivity extends CheckPermissionsActivity {
    @ViewInject(R.id.id_top_back_iv_img)
    ImageView mivBackImg;
    @ViewInject(R.id.id_top_back_tv)
    TextView mtvTopBackContent;
    @ViewInject(R.id.id_top_tv_content)
    TextView mtvTopContent;
    @ViewInject(R.id.id_top_rl)
    RelativeLayout mrlTop;
    @ViewInject(R.id.id_ac_clipimage_clipimagelayout)
    private ClipImageLayout mClipImageLayout;
    @ViewInject(R.id.id_ac_clip_btn_confirm)
    private Button mbtnSavePhoto;
    private String path = "";
    private ProgressDialog mProgressDialog;
    private String[] permissionarray = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_PHONE_STATE,
    };
    private Context mContext;

    @Event(value = {R.id.id_top_back_iv_img, R.id.id_top_back_tv, R.id.id_ac_clip_btn_confirm}, type = View.OnClickListener.class)
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_top_back_iv_img:
            case R.id.id_top_back_tv:
                finish();
                break;
            case R.id.id_ac_clip_btn_confirm:
                mProgressDialog.show();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        Bitmap clipBitmap = mClipImageLayout.clip();
                        File file = new File(XDApplication.msSavePhth, "crop_photo");
                        if (!file.exists()) {
                            file.mkdir();
                        }
                        String savePath = XDApplication.msSavePhth
                                + "/crop_photo/" + System.currentTimeMillis() + ".png";
                        ImageTools.saveBitmapToSDCard(clipBitmap, savePath);
                        mProgressDialog.dismiss();
                        Intent intent = new Intent();
                        intent.putExtra("path", savePath);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }.start();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle_permission = new Bundle();
        bundle_permission.putStringArray("permission", permissionarray);
        super.onCreate(bundle_permission);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);
        initView();
        //这步必须要加
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("请稍后...");
        path = getIntent().getStringExtra("path");
        if (path != null)
        if (TextUtils.isEmpty(path) || !(new File(path).exists())) {
            Toast.makeText(this, getResources().getString(R.string.photo_load_fail), Toast.LENGTH_SHORT).show();
            /*****************************可以用默认加载图片**************************************/
            return;
        }
        Bitmap bitmap = ImageTools.convertToBitmap(path, 800, 1000);
        if (bitmap == null) {
            Toast.makeText(this, getResources().getString(R.string.photo_load_fail), Toast.LENGTH_SHORT).show();
            /*****************************可以用默认加载图片**************************************/
            return;
        }
        mClipImageLayout.setBitmap(bitmap);
    }

    private void initView() {
        mContext = ClipActivity.this;
        mtvTopContent.setText(mContext.getResources().getString(R.string.move_scale));
        mivBackImg.setImageResource(R.drawable.go_back_white);
        mrlTop.setBackgroundColor(mContext.getResources().getColor(R.color.background_color_thin_green));
        mtvTopBackContent.setText(mContext.getResources().getString(R.string.register));
    }
}
