package com.tyb.xd.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tyb.xd.R;
import com.tyb.xd.fragment.FgCommunicate;
import com.tyb.xd.fragment.FgHall;
import com.tyb.xd.fragment.FgMy;
import com.tyb.xd.service.LocalLocationService;
import com.tyb.xd.service.LocalLocationServiceConnection;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 主界面
 */
@ContentView(R.layout.ac_home)
public class HomeActivity extends FragmentActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback {

    @ViewInject(R.id.id_home_fl)
    private FrameLayout mflhome;

    @ViewInject(R.id.id_homedown_hall_iv_img)
    private ImageView mivHall;

    @ViewInject(R.id.id_homedown_hall_tv)
    private TextView mtvHall;

    @ViewInject(R.id.id_homedown_communicate_iv)
    private ImageView mivCommunicate;

    @ViewInject(R.id.id_homedown_communicate_tv)
    private TextView mtvCommunicate;

    @ViewInject(R.id.id_homedown_my_iv)
    private ImageView mivMy;

    @ViewInject(R.id.id_homedown_my_tv)
    private TextView mtvMy;


    private FgCommunicate mfgCommunicate;
    private FgHall mfgHall;
    private FgMy mfgMy;

    private int mGreenColor;
    private int mGrayColor;

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET
    };

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    private static final int PERMISSON_REQUESTCODE = 0;

    private LocalLocationServiceConnection mLocalConnection;

    @Event(value = {R.id.id_homedown_hall_iv_img, R.id.id_homedown_hall_tv,
            R.id.id_homedown_communicate_iv, R.id.id_homedown_communicate_tv,
            R.id.id_homedown_my_iv, R.id.id_homedown_my_tv}, type = View.OnClickListener.class)
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_homedown_hall_iv_img:
            case R.id.id_homedown_hall_tv:
                setSelect(0);
                break;
            case R.id.id_homedown_communicate_iv:
            case R.id.id_homedown_communicate_tv:
                setSelect(1);
                break;
            case R.id.id_homedown_my_iv:
            case R.id.id_homedown_my_tv:
                setSelect(2);
                break;
        }
    }

    private void setSelect(int positon) {
        FragmentManager fgManager = getSupportFragmentManager();
        FragmentTransaction tgTransaction = fgManager.beginTransaction();
        hideFragment(tgTransaction);
        setTab(positon, tgTransaction);
        tgTransaction.commit();
    }

    private void setTab(int positon, FragmentTransaction fgTransaction) {
        resetTab();
        switch (positon) {
            case 0:
                if (mfgHall == null) {
                    mfgHall = FgHall.newInstance();
                    fgTransaction.add(R.id.id_home_fl, mfgHall);
                }
                fgTransaction.show(mfgHall);
                mivHall.setImageResource(R.drawable.hall_green);
                mtvHall.setTextColor(mGreenColor);
                break;
            case 1:
                if (mfgCommunicate == null) {
                    mfgCommunicate = FgCommunicate.newInstance();
                    fgTransaction.add(R.id.id_home_fl, mfgCommunicate);
                }
                fgTransaction.show(mfgCommunicate);
                mivCommunicate.setImageResource(R.drawable.news_green);
                mtvCommunicate.setTextColor(mGreenColor);
                break;
            case 2:
                if (mfgMy == null) {
                    mfgMy = FgMy.newInstance();
                    fgTransaction.add(R.id.id_home_fl, mfgMy);
                }
                fgTransaction.show(mfgMy);
                mivMy.setImageResource(R.drawable.my_green);
                mtvMy.setTextColor(mGreenColor);
                break;
        }
    }

    /**
     * 重置底部按钮和文字
     */
    private void resetTab() {
        mivCommunicate.setImageResource(R.drawable.news_gray);
        mtvCommunicate.setTextColor(mGrayColor);
        mivHall.setImageResource(R.drawable.hall_gray);
        mtvHall.setTextColor(mGrayColor);
        mivMy.setImageResource(R.drawable.my_gray);
        mtvMy.setTextColor(mGrayColor);
    }


    private void hideFragment(FragmentTransaction tgTransaction) {
        if (mfgCommunicate != null) {
            tgTransaction.hide(mfgCommunicate);
        }
        if (mfgHall != null) {
            tgTransaction.hide(mfgHall);
        }
        if (mfgMy != null) {
            tgTransaction.hide(mfgMy);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);
        mGreenColor = getResources().getColor(R.color.text_color_theme_green);
        mGrayColor = getResources().getColor(R.color.text_color_thin_gray);
        setSelect(0);
        mLocalConnection = new LocalLocationServiceConnection();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedCheck) {
            checkPermissions(needPermissions);
        }
        startLocalService();
        if (mfgMy != null) {
            /**
             * 进行我的界面的数据更新
             */
        }
        if (mfgHall != null) {
            /**
             * 进行悬赏大厅的界面的数据更新
             */
        }
        if (mfgCommunicate != null) {
            /**
             * 进行交流界面的数据更新
             */
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mLocalConnection);
    }

    private void startLocalService() {
        try {
            Intent intent = new Intent(HomeActivity.this, LocalLocationService.class);
            bindService(intent, mLocalConnection, BIND_AUTO_CREATE);
        } catch (Exception e) {

        }
    }

    /**
     * @param
     * @since 2.5.0
     */
    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList
                && needRequestPermissonList.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    needRequestPermissonList.toArray(
                            new String[needRequestPermissonList.size()]),
                    PERMISSON_REQUESTCODE);
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED) {
                needRequestPermissonList.add(perm);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this, perm)) {
                    needRequestPermissonList.add(perm);
                }
            }
        }
        return needRequestPermissonList;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                showMissingPermissionDialog();
                isNeedCheck = false;
            }
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.notifyTitle);
        builder.setMessage(R.string.notifyMsg);

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        builder.setPositiveButton(R.string.setting,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });

        builder.setCancelable(false);

        builder.show();
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
