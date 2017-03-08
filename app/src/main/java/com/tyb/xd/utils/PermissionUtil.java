package com.tyb.xd.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

/**
 * Created by wangpeiyu on 2016/7/4.
 */
public class PermissionUtil {

    public static String permission[]=new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAPTURE_AUDIO_OUTPUT,
            Manifest.permission.MODIFY_AUDIO_SETTINGS};

    /**
     * 检查指定权限是否开启，未开启则开启
     * @param context
     * @param permissionArray 要检查的权限数组
     */
    public static void checkPermission(Context context, String... permissionArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < permissionArray.length; i++) {
                String permission = permissionArray[i];
                if (ActivityCompat.checkSelfPermission(context,
                        permission) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                            permission)) {
                    } else {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission}, i);
                    }
                }
            }
        } else {
            return;
        }
    }

    /**
     * 检查该系统的所需要的所有权限
     * @param context
     */
    public static void checkPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < PermissionUtil.permission.length; i++) {
                String permission = PermissionUtil.permission[i];
                if (ActivityCompat.checkSelfPermission(context,
                        permission) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                            permission)) {
                    } else {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission}, i);
                    }
                }
            }
        } else {
            return;
        }
    }
}
